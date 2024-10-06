package com.backend.dodoesdidserver.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class EmailVerifyDto {

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "인증번호", nullable = true)
    private String verifyCode;

}
