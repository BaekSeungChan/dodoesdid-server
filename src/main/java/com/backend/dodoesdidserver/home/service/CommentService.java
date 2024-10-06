package com.backend.dodoesdidserver.home.service;

import com.amazonaws.services.s3.AmazonS3;
import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.home.domain.Dazim;
import com.backend.dodoesdidserver.home.domain.DazimComment;
import com.backend.dodoesdidserver.home.domain.ReplyComment;
import com.backend.dodoesdidserver.home.dto.request.DazimCommentRequest;
import com.backend.dodoesdidserver.home.dto.request.ReplyCommentRequest;
import com.backend.dodoesdidserver.home.dto.response.DazimDetailCommnent;
import com.backend.dodoesdidserver.home.dto.response.ReplyCommentResponse;
import com.backend.dodoesdidserver.home.repository.DazimCommentRepository;
import com.backend.dodoesdidserver.home.repository.DazimRepository;
import com.backend.dodoesdidserver.home.repository.ReplyCommentRepository;
import com.backend.dodoesdidserver.user.entity.User;
import com.backend.dodoesdidserver.user.entity.UserImage;
import com.backend.dodoesdidserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    private final DazimCommentRepository dazimCommentRepository;
    private final DazimRepository dazimRepository;
    private final UserRepository userRepository;
    private final ReplyCommentRepository replyCommentRepository;

    @Transactional
    public void createDazimComment(DazimCommentRequest request, String email){

        User user = userRepository.findByEmail(email);

        Dazim dazim = dazimRepository.findById(request.getDazimId())
                .orElseThrow(() -> new ApiException(ErrorCode.DAZIM_NOT_FOUND_ERROR));

        DazimComment dazimComment = DazimComment.builder()
                .dazim(dazim)
                .user(user)
                .content(request.getContent())
                .build();

        dazimCommentRepository.save(dazimComment);
    }

    public List<DazimDetailCommnent> getCommentsByDazim(String email, Long dazimId) {

        Dazim  dazim = dazimRepository.findById(dazimId)
                        .orElseThrow(() -> new ApiException(ErrorCode.DAZIM_NOT_FOUND_ERROR));

        List<DazimComment> dazimComments = dazimCommentRepository.findByDazim(dazim);

        LocalDateTime now = LocalDateTime.now();

       return dazimComments.stream().map(dazimComment -> {
           String timeAgo = calculateTimeAgo(dazimComment.getCreatedDateTime(), now);

           User commentUser = dazimComment.getUser();

           String profileImageUrl = null;
           List<UserImage> userImages = commentUser.getUserImages();

           if (userImages != null && !userImages.isEmpty()) {
               UserImage userImage = userImages.get(0);
               profileImageUrl = amazonS3.getUrl(bucketName, userImage.getImagePath()).toString();
           }

           return DazimDetailCommnent.builder()
                   .commentContent(dazimComment.getContent())
                   .commentNickName(commentUser.getNickname())
                   .commentId(dazimComment.getId())
                   .commentUserEmail(commentUser.getEmail())
                   .commentTimeAgo(timeAgo)
                   .userImageUrl(profileImageUrl)
                   .build();
       }).toList();
    }


    @Transactional
    public void updateDazimComment(Long commentId, String newContent, String email) {
        DazimComment dazimComment = dazimCommentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND_ERROR));

        if(!dazimComment.getUser().getEmail().equals(email)){
            throw new ApiException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        dazimComment.changeDazimContent(newContent);
        dazimCommentRepository.save(dazimComment);
    }

    @Transactional
    public void deleteDazimComment(Long commentId, String email) {
        DazimComment dazimComment = dazimCommentRepository.findById(commentId)
               .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND_ERROR));

        if(!dazimComment.getUser().getEmail().equals(email)){
            throw new ApiException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        dazimCommentRepository.delete(dazimComment);
    }

    @Transactional
    public void createReplyComment(String email, Long commentId, ReplyCommentRequest request) {

        User user = userRepository.findByEmail(email);

        DazimComment dazimComment = dazimCommentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND_ERROR));

        ReplyComment replyComment = ReplyComment.builder()
                .content(request.getContent())
                .user(user)
                .dazimComment(dazimComment)
                .build();

        replyCommentRepository.save(replyComment);
    }

    public List<ReplyCommentResponse> getReplyByComments(String email, Long commentId) {
        DazimComment dazimComment = dazimCommentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND_ERROR));

        List<ReplyComment> replyComments = dazimComment.getReplies();

        LocalDateTime now = LocalDateTime.now();

        return replyComments.stream().map(replyComment -> {
            String timeAgo = calculateTimeAgo(replyComment.getCreatedDateTime(), now);

            User user = replyComment.getUser();

            String userImagePath = user.getUserImages().stream().map(userImage ->userImage.getImagePath()).findFirst().orElse(null);

            String profileImageUrl = amazonS3.getUrl(bucketName, userImagePath).toString();

            return ReplyCommentResponse.builder()
                   .replyContent(replyComment.getContent())
                   .replyNickName(replyComment.getUser().getNickname())
                   .replyId(replyComment.getId())
                   .replyUserEmail(replyComment.getUser().getEmail())
                    .replyUserImage(profileImageUrl)
                   .replyTimeAgo(timeAgo.toString())
                   .build();
        }).toList();
    }

    @Transactional
    public void updateReplyComment(Long replyId, ReplyCommentRequest newContent, String email) {
        ReplyComment replyComment = replyCommentRepository.findById(replyId)
                .orElseThrow(() -> new ApiException(ErrorCode.REPLY_COMMENT_NOT_FOUND_ERROR));

        if(!replyComment.getUser().getEmail().equals(email)){
            throw new ApiException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        replyComment.changeReplyContent(newContent.getContent());
        replyCommentRepository.save(replyComment);
    }

    @Transactional
    public void deleteDazimReplyComment(Long replyId, String email) {
        ReplyComment replyComment = replyCommentRepository.findById(replyId)
                .orElseThrow(() -> new ApiException(ErrorCode.REPLY_COMMENT_NOT_FOUND_ERROR));

        if(!replyComment.getUser().getEmail().equals(email)){
            throw new ApiException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        replyCommentRepository.delete(replyComment);
    }


    private String calculateTimeAgo(LocalDateTime createdAt, LocalDateTime now) {
        Duration duration = Duration.between(createdAt, now);

        long minutes = duration.toMinutes();
        long hours = duration.toHours();

        if (minutes < 60) {
            return minutes + "분 전";  // 1시간 미만일 경우 분 단위로 표시
        } else {
            return hours + "시간 전";  // 1시간 이상일 경우 시간 단위로 표시
        }
    }



}
