package com.backend.dodoesdidserver.email.controller;

import com.amazonaws.services.kms.model.VerifyRequest;
import com.backend.dodoesdidserver.common.ApiCustomResponse;
import com.backend.dodoesdidserver.email.dto.EmailVerifyDto;
import com.backend.dodoesdidserver.email.dto.RequestVerifyDto;
import com.backend.dodoesdidserver.email.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Email API", description = "email 컨트롤러에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "이메일 인증번호 발송", description = "회원가입시 이메일 인증번호 발송 기능입니다",
           responses = {
               @ApiResponse(responseCode = "200", description = "정상 발송",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiCustomResponse.class))),
               @ApiResponse(responseCode = "401", description = "발송 실패",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiCustomResponse.class)))
           })
    @PostMapping("/send")
    public ApiCustomResponse sendEmail(@RequestBody RequestVerifyDto dto) throws MessagingException {
        emailService.sendEmail(dto.getEmail());
        return ApiCustomResponse.OK("인증코드가 발송되었습니다.");
    }

    @Operation(summary = "이메일 인증번호 확인", description = "이메일 인증번호 확인 기능입니다",
           responses = {
               @ApiResponse(responseCode = "200", description = "정상 인증",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiCustomResponse.class))),
               @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiCustomResponse.class)))
           })
    @PostMapping("/verify")
    public ApiCustomResponse verifyEmail(@RequestBody EmailVerifyDto dto) {
        boolean isVerify = emailService.verifyEmailCode(dto.getEmail(), dto.getVerifyCode());
        return isVerify ? ApiCustomResponse.OK("인증이 완료되었습니다.") : ApiCustomResponse.ERROR("인증에 실패하였습니다.");

    }

    @Operation(summary = "비밀번호 재설정 이메일 발송", description = "비밀번호 재설정 링크를 메일로 보내는 기능입니다. 링크 뒤에 보내드리는 토큰으로 비밀번호 재설정을 진행해 주세요.",
           responses = {
               @ApiResponse(responseCode = "200", description = "정상 인증",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiCustomResponse.class))),
               @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiCustomResponse.class)))
           })
    @PostMapping("/reset-email")
    public ApiCustomResponse sendResetMail(@RequestBody RequestVerifyDto dto) throws MessagingException {
        emailService.sendResetMail(dto.getEmail());
        return ApiCustomResponse.OK("비밀번호 재설정 메일이 발송되었습니다.");
    }

}
