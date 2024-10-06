package com.backend.dodoesdidserver.feed.dto.response;

import com.backend.dodoesdidserver.home.dto.response.DazimCommentResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedDetailResponseDto {
    private String userName;  // 다짐 작성자 닉네임
    private String dazimContent;  // 다짐 내용
    private String dazimContentId;  // 다짐 ID
    private String dazimFileUrl;  // 다짐 이미지 URL
    private String dazimTimeAgo;  // 다짐 작성 시간 (상대적 시간)
    private int dodoesdid1;  // 첫 번째 좋아요 카운트
    private int dodoesdid2;  // 두 번째 좋아요 카운트
    private int dodoesdid3;  // 세 번째 좋아요 카운트
    private int dodoesdid4;  // 네 번째 좋아요 카운트
    private int dodoesdid5;  // 다섯 번째 좋아요 카운트
    private List<DazimCommentResponseDTO> dazimComments;  // 다짐에 달린 댓글 목록
    private String userProfileImageUrl;  // 다짐 작성자 프로필 이미지 URL
}
