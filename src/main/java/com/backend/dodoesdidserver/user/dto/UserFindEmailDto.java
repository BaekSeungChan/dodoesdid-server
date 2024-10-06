package com.backend.dodoesdidserver.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserFindEmailDto {

    @Schema(description = "사용자 전화번호", example = "01012345678")
    @Pattern(regexp = "^(01[0|1|6|7|8|9]\\d{3,4}\\d{4}|02\\d{3,4}\\d{4}|0[3-9][0-9]\\d{3,4}\\d{4})$", message = "전화번호 형식이 올바르지 않습니다.")
    private String userPhone;

}
