package com.backend.dodoesdidserver.home.dto.response;


import com.backend.dodoesdidserver.home.domain.ReplyComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DazimCommentResponseDTO {
    private String commentUserImage;
    private String commentUserNickName;
    private String commentContent;
    private String commentTimeAgo;
    private String commentUserEmail;
    private Long commentId;
    private List<ReplyCommentResponse> replyComments;
}
