package com.backend.dodoesdidserver.home.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DazimCommentRequest {

    private String content;
    private Long dazimId;

}
