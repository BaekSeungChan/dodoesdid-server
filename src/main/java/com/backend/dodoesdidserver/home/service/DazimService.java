package com.backend.dodoesdidserver.home.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.group.domain.Group;
import com.backend.dodoesdidserver.group.domain.GroupMember;
import com.backend.dodoesdidserver.group.repository.GroupMemberRepository;
import com.backend.dodoesdidserver.group.repository.GroupRepository;
import com.backend.dodoesdidserver.home.domain.Dazim;
import com.backend.dodoesdidserver.home.domain.DazimImage;
import com.backend.dodoesdidserver.home.domain.DazimLike;
import com.backend.dodoesdidserver.home.dto.request.DazimContentRequest;
import com.backend.dodoesdidserver.home.dto.request.DazimImageRequest;
import com.backend.dodoesdidserver.home.dto.response.DazimRequestResponse;
import com.backend.dodoesdidserver.home.repository.DazimImageRepository;
import com.backend.dodoesdidserver.home.repository.DazimRepository;
import com.backend.dodoesdidserver.home.repository.custom.DazimLikeRepository;
import com.backend.dodoesdidserver.user.entity.User;
import com.backend.dodoesdidserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DazimService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3 amazonS3;

    private final DazimRepository dazimRepository;
    private final DazimImageRepository dazimImageRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final DazimLikeRepository dazimLikeRepository;

    @Transactional
    public void writeDazimContent(DazimContentRequest request, String email, String groupName){

        User user = userRepository.findByEmail(email);
        Group group = groupRepository.findByGroupName(groupName);
        GroupMember groupMember = groupMemberRepository.findByUserAndGroup(user, group);


        if(groupMember == null){
            throw new ApiException(ErrorCode.GROUP_USER_NOT_FOUND_ERROR);
        }


        Dazim dazim = Dazim.builder()
                .dazimContent(request.getDazimContent())
                .groupMember(groupMember)
                .build();

        dazimRepository.save(dazim);
    }


    @Transactional
    public void uploadDzaimImage(DazimImageRequest dazimImages, Long dazimId, Long groupMemberId) {
        MultipartFile dazimImageFile = dazimImages.getDazimFiles();

        String originalFileName = dazimImageFile.getOriginalFilename();
        String s3FileName = upload(dazimImageFile); // s3 저장파일 명

        GroupMember groupMember = groupMemberRepository.findById(groupMemberId)
                .orElseThrow(() -> new ApiException(ErrorCode.GROUP_USER_NOT_FOUND_ERROR));

        Dazim dazim = dazimRepository.findByIdAndGroupMember(dazimId, groupMember);

        if(dazim == null){
            throw new ApiException(ErrorCode.DAZIM_NOT_FOUND_ERROR);
        }

        DazimImage image = DazimImage.createDazimImage(originalFileName, s3FileName, dazim);

        dazimImageRepository.save(image);
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


    @Transactional
    public void dodoesPresssButton(Long dazimId, String email, int dodoesLikeNum) {

        User user = userRepository.findByEmail(email);

        Dazim dazim = dazimRepository.findById(dazimId)
                .orElseThrow(() -> new ApiException(ErrorCode.DAZIM_NOT_FOUND_ERROR));


        DazimLike dazimLike = dazimLikeRepository.findByDazimAndUserAndButtonNumber(dazim, user, dodoesLikeNum)
                .orElse(null);

        if(dazimLike == null){
            dazimLike = new DazimLike(dazim, user, dodoesLikeNum,true);
            dazim.increaseButtonCount(dodoesLikeNum);
        } else {
            if(dazimLike.isPressed()){
                dazimLike.changePressed(false);
                dazim.decreaseButtonCount(dodoesLikeNum);
            } else {
                dazimLike.changePressed(true);
                dazim.increaseButtonCount(dodoesLikeNum);
            }
        }

        dazimLikeRepository.save(dazimLike);
        dazimRepository.save(dazim);
    }
}
