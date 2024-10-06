package com.backend.dodoesdidserver.group.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.group.domain.Group;
import com.backend.dodoesdidserver.group.domain.GroupImage;
import com.backend.dodoesdidserver.group.domain.GroupMember;
import com.backend.dodoesdidserver.group.domain.GroupRole;
import com.backend.dodoesdidserver.group.dto.request.GroupImageUpdateDto;
import com.backend.dodoesdidserver.group.dto.request.GroupNameRequestDto;
import com.backend.dodoesdidserver.group.dto.request.GroupNoticeDto;
import com.backend.dodoesdidserver.group.dto.request.GroupRequestDto;
import com.backend.dodoesdidserver.group.dto.response.GroupMemberListResponseDto;
import com.backend.dodoesdidserver.group.dto.response.GroupResponseDto;
import com.backend.dodoesdidserver.group.dto.response.UserGroupListResponseDto;
import com.backend.dodoesdidserver.group.repository.GroupImageRepository;
import com.backend.dodoesdidserver.group.repository.GroupMemberRepository;
import com.backend.dodoesdidserver.group.repository.GroupRepository;
import com.backend.dodoesdidserver.home.domain.Dazim;
import com.backend.dodoesdidserver.home.domain.DazimSucess;
import com.backend.dodoesdidserver.home.dto.response.DazimHomeResponse;
import com.backend.dodoesdidserver.home.dto.response.HomeResponseDto;
import com.backend.dodoesdidserver.image.service.S3ImageService;
import com.backend.dodoesdidserver.user.dto.UserGroupDetailDto;
import com.backend.dodoesdidserver.user.entity.User;
import com.backend.dodoesdidserver.user.repository.UserRepository;
import com.backend.dodoesdidserver.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3 amazonS3;

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupImageRepository groupImageRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public GroupResponseDto createGroup(String email, GroupRequestDto groupRequestDto) {

        String groupName = groupRequestDto.getGroupName();

        if(groupRepository.existsByGroupName(groupName)){
            throw new ApiException(ErrorCode.GROUP_NAME_ALREADY_EXISTS);
        }

        Group group = Group.createGroup(groupName);
        groupRepository.save(group);

        String storedFileName = upload(groupRequestDto.getGroupImage());

        String originalFileName = groupRequestDto.getGroupImage().getOriginalFilename();
        GroupImage groupImage = GroupImage.createGroupImage(group, originalFileName, storedFileName);
        groupImageRepository.save(groupImage);


        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        GroupMember groupMember = GroupMember.builder()
                .user(user)
                .group(group)
                .role(GroupRole.ADMIN)
                .build();

        groupMemberRepository.save(groupMember);

        // URL 생성
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/group/{groupName}")
                .buildAndExpand(groupName)
                .toUriString();

        group.changeGroupUrl(url);

        return GroupResponseDto.toGroupDto(groupName, url);

    }




    @Transactional
    public void joinGroup(String email, String groupName) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        Group group = groupRepository.findByGroupName(groupName);

        if (group == null) {
            throw new ApiException(ErrorCode.GROUP_NOT_FOUND);
        }

        // 유저가 이미 그룹에 가입되어 있는지 확인
        boolean existedUser = groupMemberRepository.existsByGroupAndUser(group, user);

        if (existedUser) {
            throw new ApiException(ErrorCode.USER_ALREADY_IN_GROUP);
        }

        GroupMember groupMember = GroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupRole.MEMBER)
                .build();

        groupMemberRepository.save(groupMember);
    }

    public List<HomeResponseDto> findAllGroup(String email) {

        User user = userRepository.findByEmail(email);

        List<GroupMember> groupMembers = groupMemberRepository.findAllByUser(user);

        List<Group> groups = groupMembers.stream().map(GroupMember::getGroup).toList();

        return groups.stream().map(group -> {
            String storedFileName = group.getGroupImages().stream()
                    .findFirst()
                    .map(image -> image.getStoredFileName())
                    .orElse(null);

            String groupUrl = (storedFileName != null)
                    ? amazonS3.getUrl(bucketName, storedFileName).toString()
                    : "이미지가 존재하지 않습니다.";

            return new HomeResponseDto(group.getId(), groupUrl, group.getGroupName());
        }).toList();
    }

    public List<DazimHomeResponse> findByGroupMember(Long groupId, LocalDate date) {

//        Group group = groupRepository.findByGroupName(groupName);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND_ERROR));

        if (group == null) {
            throw new ApiException(ErrorCode.GROUP_NOT_FOUND_ERROR);
        }

        List<GroupMember> groupMembers = group.getGroupMembers();

        List<DazimHomeResponse> dazimResponses = groupMembers.stream()
                .map(groupMember -> {
                    User user = groupMember.getUser();
                    String nickName = user.getNickname();
                    String userImage = user.getUserImages().stream()
                            .findFirst()
                            .map(image -> image.getImagePath())
                            .orElse(null);

                    Long groupMemberId = groupMember.getId();

                    Dazim matchingDazim = groupMember.getDazims().stream()
                            .filter(dazim -> dazim.getCreatedDateTime().toLocalDate().equals(date))
                            .findFirst()
                            .orElse(null);

                    if (matchingDazim == null) {
                        return new DazimHomeResponse(
                                nickName,
                                userImage,
                                "다짐이 없습니다.",
                                null,
                                null,
                                groupMemberId,
                                "다짐 전 두더지"
                        );
                    }

                    String dazimContent = matchingDazim.getDazimContent() != null ? matchingDazim.getDazimContent() : "다짐이 없습니다.";

                    String dazimImage = matchingDazim.getDazimImages().stream()
                            .findFirst()
                            .map(image -> image.getStoredFileName())
                            .orElse(null);

                    String S3ImageUrl = amazonS3.getUrl(bucketName, dazimImage).toString();

                    Long dazimId = matchingDazim.getId();

                    String dodoesSuccessImage = matchingDazim.getDazimImages().stream()
                            .findFirst()
                            .map(image -> {
                                if(image.getDazimSucess() == DazimSucess.SUCESS){
                                    return "다짐 달성 완료 두더지";
                                }
                                    return "다짐 입력 후 두더지";
                            })
                            .orElse("다짐 입력 후 두더지");

                    return new DazimHomeResponse(
                            nickName,
                            userImage,
                            dazimContent,
                            S3ImageUrl,
                            dazimId,
                            groupMemberId,
                            dodoesSuccessImage
                    );
                }).toList();

        return dazimResponses;
    }


    public String upload(MultipartFile image){
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
            throw new ApiException(ErrorCode.EMPTY_FILE_EXCEPTION);
        }
        return this.uploadImage(image);
    }

    public String uploadImage(MultipartFile image){
        this.validateImageFileExtenstion(image.getOriginalFilename());
        try{
            return this.uploadImageToS3(image);
        } catch (IOException e){
            throw new ApiException(ErrorCode.IO_EXCEPTION_ON_IMAGE_UPLOAD);
        }
    }

    public void validateImageFileExtenstion(String filename){
        int lastDotIndex = filename.lastIndexOf(".");
        if(lastDotIndex == -1){
            throw new ApiException(ErrorCode.NO_FILE_EXTENSION);
        }

        String extension = filename.substring(lastDotIndex  + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if(!allowedExtentionList.contains(extension)){
            throw new ApiException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFileName = image.getOriginalFilename(); // 원본 파일 명
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFileName; // 변경된 파일 명

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.PUT_OBJECT_EXCEPTION);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

//        return amazonS3.getUrl(bucketName, s3FileName).toString();
        return s3FileName;
    }

    public List<UserGroupListResponseDto> getUserGroupList(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        List<GroupMember> userGroups = groupMemberRepository.findAllByUser(user);
        List<UserGroupListResponseDto> responseDtoList = new ArrayList<>();
        for(GroupMember userGroup : userGroups){
            String groupName = userGroup.getGroup().getGroupName();
            Long groupId = userGroup.getGroup().getId();
            String groupImage = groupImageRepository.findByGroup(userGroup.getGroup()).get().getStoredFileName();
            responseDtoList.add(UserGroupListResponseDto.builder().groupId(groupId).groupImage(groupImage).groupName(groupName).build());
        }
        return responseDtoList;
    }

    public UserGroupDetailDto getUserGroupDetail(Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));
        String storedFileName = groupImageRepository.findByGroup(group).get().getStoredFileName();
        String groupImage = (storedFileName != null)
                ? amazonS3.getUrl(bucketName, storedFileName).toString()
                : "이미지가 존재하지 않습니다.";

        return UserGroupDetailDto.builder().groupId(groupId).groupName(group.getGroupName()).groupImage(groupImage).groupNotice(group.getGroupNotice()).groupUrl(group.getGroupUrl()).build();
    }

    public List<GroupMemberListResponseDto> findBySameGroupMember(Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));
        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroup(group);
        List<GroupMemberListResponseDto> responseDtoList = new ArrayList<>();
        for(GroupMember groupMember : groupMembers){
            responseDtoList.add(GroupMemberListResponseDto.builder().userId(groupMember.getUser().getId()).groupId(groupMember.getGroup().getId()).userName(groupMember.getUser().getName()).groupRole(groupMember.getRole()).build());
        }
        return responseDtoList;
    }

    @Transactional
    public void upadateGroupName(GroupNameRequestDto dto) {
        Group group = groupRepository.findById(dto.getGroupId()).orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));
        boolean isExist = groupRepository.existsByGroupName(dto.getGroupName());
        if(isExist){
            throw new ApiException(ErrorCode.GROUP_NAME_ALREADY_EXISTS);
        }
        group.updateGroupName(dto.getGroupName());
        groupRepository.save(group);
    }

    @Transactional
    public void exitGroup(String username, Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));
        User user = userRepository.findByEmail(username);
        GroupMember groupMember = groupMemberRepository.findByUserAndGroup(user, group);
        groupMemberRepository.deleteById(groupMember.getId());
    }

    @Transactional
    public void uploadGroupNotice(GroupNoticeDto groupNoticeDto) {
        Group group = groupRepository.findById(groupNoticeDto.getGroupId()).orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));
        group.updateGroupNotice(groupNoticeDto.getGroupNotice());
        groupRepository.save(group);
    }

    @Transactional
    public void updateGroupImage(GroupImageUpdateDto dto) {
        Group group = groupRepository.findById(dto.getGroupId()).orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));
        if(dto.getGroupImage() != null){
            if(groupImageRepository.findByGroup(group) != null){
                groupImageRepository.deleteByGroup(group);
            }

            String originalFileName = groupImageRepository.findByGroup(group).get().getStoredFileName();
            String uploadUrl = s3ImageService.upload(dto.getGroupImage());
            GroupImage groupImage = GroupImage.builder().originalFileName(originalFileName).storedFileName(uploadUrl).group(group).build();

            groupImageRepository.save(groupImage);

        }
    }
}
