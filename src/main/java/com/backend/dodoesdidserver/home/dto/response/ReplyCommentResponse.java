package com.backend.dodoesdidserver.home.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyCommentResponse {

    private Long replyId;
    private String replyContent;
    private String replyNickName;
    private String replyTimeAgo;
    private String replyUserEmail;
    private String replyUserImage;

}
