package com.backend.dodoesdidserver.home.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DazimDetailCommnent {

    private Long commentId;
    private String commentContent;
    private String commentNickName;
    private String commentTimeAgo;
    private String commentUserEmail;
    private String userImageUrl;
}
