package com.backend.dodoesdidserver.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RequestVerifyDto {

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

}
