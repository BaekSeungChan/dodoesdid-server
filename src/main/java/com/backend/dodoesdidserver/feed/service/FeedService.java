package com.backend.dodoesdidserver.feed.service;

import com.amazonaws.services.s3.AmazonS3;
import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.feed.dto.response.FeedDetailResponseDto;
import com.backend.dodoesdidserver.feed.dto.response.FeedResponseDTO;
import com.backend.dodoesdidserver.group.domain.Group;
import com.backend.dodoesdidserver.group.domain.GroupMember;
import com.backend.dodoesdidserver.group.repository.GroupMemberRepository;
import com.backend.dodoesdidserver.home.domain.Dazim;
import com.backend.dodoesdidserver.home.domain.DazimComment;
import com.backend.dodoesdidserver.home.domain.DazimImage;
import com.backend.dodoesdidserver.home.domain.ReplyComment;
import com.backend.dodoesdidserver.home.dto.response.DazimCommentResponseDTO;
import com.backend.dodoesdidserver.home.dto.response.ReplyCommentResponse;
import com.backend.dodoesdidserver.home.repository.DazimRepository;
import com.backend.dodoesdidserver.user.entity.User;
import com.backend.dodoesdidserver.user.entity.UserImage;
import com.backend.dodoesdidserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3 amazonS3;

    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final DazimRepository dazimRepository;

    public List<FeedResponseDTO> findAllFeed(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }

        List<GroupMember> groupMembers = user.getGroupMembers();

        List<Group> groups = groupMembers.stream()
                .map(GroupMember::getGroup)
                .toList();

        List<GroupMember> allGroupMembers = groups.stream()
                .flatMap(group -> group.getGroupMembers().stream())
                .toList();

        List<Dazim> dazims = allGroupMembers.stream()
                .flatMap(groupMember -> groupMember.getDazims().stream())
                .toList();

        int dazimLikeCount = dazims.stream()
                .mapToInt(dazim -> dazim.getDodoesCount1()
                        + dazim.getDodoesCount2()
                        + dazim.getDodoesCount3()
                        + dazim.getDodoesCount4()
                        + dazim.getDodoesCount5())
                .sum();


        int totalCommentAndReplyCount = dazims.stream()
                .mapToInt(dazim -> dazim.getDazimCommnets().stream()
                        .mapToInt(comment -> 1 + comment.getReplies().size())  // 각 댓글과 해당 대댓글의 개수 합산
                        .sum())
                .sum();


        return dazims.stream()
                .flatMap(dazim -> dazim.getDazimImages().stream())
                .map(dazimImage -> {

                    String timeAgo = getTimeAgo(dazimImage.getCreatedDateTime());


                    Dazim dazim = dazimImage.getDazim();
                    User dazimUser = dazim.getGroupMember().getUser();

                    String profileImageUrl = dazimUser.getUserImages().stream()
                            .findFirst()
                            .map(UserImage::getImagePath)
                            .orElse(null);

                    String dazimFileUrl = amazonS3.getUrl(bucketName, dazimImage.getStoredFileName()).toString();

                    return FeedResponseDTO.builder()
                            .userName(dazimUser.getName())
                            .userProfileImageUrl(profileImageUrl)
                            .dazimContent(dazim.getDazimContent())
                            .dazimContentId(dazim.getId().toString())
                            .dazimFileUrl(dazimFileUrl)
                            .dazimLikeCount(dazimLikeCount)
                            .totalCommentAndReplyCount(totalCommentAndReplyCount)
                            .dazimTimeAgo(timeAgo)
                            .build();
                })
                .toList();
    }


    public FeedDetailResponseDto findById(String email, Long dazimContentId) {

        User user = userRepository.findByEmail(email);

        String userImage = user.getUserImages().stream().findFirst()
                .map(UserImage::getImagePath)
                .orElse(null);


        Dazim dazim = dazimRepository.findById(dazimContentId)
                .orElseThrow(() -> new ApiException(ErrorCode.DAZIM_NOT_FOUND_ERROR));

        String dazimNickeName = dazim.getGroupMember().getUser().getNickname();

        String dazimFileUrl = dazim.getDazimImages().stream()
                .findFirst()
                .map(dazimImage -> amazonS3.getUrl(bucketName, dazimImage.getStoredFileName()).toString())
                .orElse(null);

        String dazimContent = dazim.getDazimContent();

        String dazimTimeAgo = getTimeAgo(dazim.getCreatedDateTime());

        int dodoesdid1 = dazim.getDodoesCount1();
        int dodoesdid2 = dazim.getDodoesCount2();
        int dodoesdid3 = dazim.getDodoesCount3();
        int dodoesdid4 = dazim.getDodoesCount4();
        int dodoesdid5 = dazim.getDodoesCount5();


        List<DazimCommentResponseDTO> dazimComments = dazim.getDazimCommnets().stream()
                .map(dazimComment -> {
                    String commentUserImage = dazimComment.getUser().getUserImages().stream()
                            .findFirst()
                            .map(UserImage::getImagePath)
                            .orElse(null);
                    String commentUserNickName = dazimComment.getUser().getNickname();
                    String commentContent = dazimComment.getContent();
                    String commentTimeAgo = getTimeAgo(dazimComment.getCreatedDateTime());
                    String commentUserEmail = dazimComment.getUser().getEmail();
                    Long commentId = dazimComment.getId();

                    List<ReplyCommentResponse> replyCommentResponses = dazimComment.getReplies().stream()
                            .map(replyComment -> ReplyCommentResponse.builder()
                                    .replyId(replyComment.getId())
                                    .replyUserImage(replyComment.getUser().getUserImages().stream().findFirst().map(UserImage::getImagePath).orElse(null))
                                    .replyNickName(replyComment.getUser().getNickname())
                                    .replyContent(replyComment.getContent())
                                    .replyUserEmail(replyComment.getUser().getEmail())
                                    .replyTimeAgo(getTimeAgo(replyComment.getCreatedDateTime()))
                                    .build()
                            )
                            .toList();

                    return DazimCommentResponseDTO.builder()
                            .commentUserImage(commentUserImage)
                            .commentUserNickName(commentUserNickName)
                            .commentContent(commentContent)
                            .commentUserEmail(commentUserEmail)
                            .commentTimeAgo(commentTimeAgo)
                            .commentId(commentId)
                            .replyComments(replyCommentResponses)
                            .build();
                })
                .toList();



        FeedDetailResponseDto feedDetailResponseDto = FeedDetailResponseDto.builder()
                .userName(dazimNickeName)
                .dazimTimeAgo(dazimTimeAgo)
                .dazimFileUrl(dazimFileUrl)
                .dazimContent(dazimContent)
                .dazimContentId(dazim.getId().toString())
                .dodoesdid1(dodoesdid1)  // 첫 번째 좋아요 수
                .dodoesdid2(dodoesdid2)  // 두 번째 좋아요 수
                .dodoesdid3(dodoesdid3)  // 세 번째 좋아요 수
                .dodoesdid4(dodoesdid4)  // 네 번째 좋아요 수
                .dodoesdid5(dodoesdid5)  // 다섯 번째 좋아요 수
                .dazimComments(dazimComments)
                .userProfileImageUrl(userImage)
                .build();

        return feedDetailResponseDto;
    }

    public String getTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);
        long minutes = duration.toMinutes();
        long hours = duration.toHours();

        if (minutes < 60) {
            return minutes + "분 전";
        } else {
            return hours + "시간 전";
        }
    }

}
