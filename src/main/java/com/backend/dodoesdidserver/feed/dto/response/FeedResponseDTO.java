package com.backend.dodoesdidserver.feed.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FeedResponseDTO {

    private String userName;
    private String userProfileImageUrl;
    private String dazimContent;
    private String dazimContentId;
    private String dazimFileUrl;
    private String dazimTimeAgo;
    private int dazimLikeCount;
    private int totalCommentAndReplyCount;

}
