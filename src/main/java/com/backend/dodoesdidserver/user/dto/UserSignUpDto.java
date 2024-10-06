package com.backend.dodoesdidserver.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;


import io.swagger.v3.oas.annotations.media.Schema;

@Getter
public class UserSignUpDto {

    @Schema(description = "사용자 이메일", example = "user@example.com")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String userEmail;

    @Schema(description = "비밀번호", example = "Password123!")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @Schema(description = "사용자 이름", example = "홍길동")
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,12}$", message = "이름은 2자 이상 12자 이하의 한글 또는 영문으로 입력해주세요.")
    private String userName;

    @Schema(description = "사용자 닉네임", example = "길동이")
    @Size(min = 2, max = 15, message = "닉네임은 2자 이상 15자 이하로 입력해주세요.")
    private String userNickname;

    @Schema(description = "사용자 생일 (YYYY-MM-DD 형식)", example = "1900-01-01")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생일 형식이 올바르지 않습니다. (YYYY-MM-DD)")
    private String userBirth;

    @Schema(description = "사용자 전화번호", example = "01012345678")
    @Pattern(regexp = "^(01[0|1|6|7|8|9]\\d{3,4}\\d{4}|02\\d{3,4}\\d{4}|0[3-9][0-9]\\d{3,4}\\d{4})$", message = "전화번호 형식이 올바르지 않습니다.")
    private String userPhone;
}

