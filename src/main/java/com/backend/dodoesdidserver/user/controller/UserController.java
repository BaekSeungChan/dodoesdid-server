package com.backend.dodoesdidserver.user.controller;

import com.backend.dodoesdidserver.common.ApiCustomResponse;
import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.user.dto.*;
import com.backend.dodoesdidserver.user.entity.User;
import com.backend.dodoesdidserver.user.repository.UserRepository;
import com.backend.dodoesdidserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Tag(name = "User API", description = "user" + "컨트롤러에 대한 API입니다.")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "회원가입", description = "회원가입 기능입니다",
           responses = {
               @ApiResponse(responseCode = "200", description = "정상 가입",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiCustomResponse.class))),
               @ApiResponse(responseCode = "401", description = "가입 실패",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiCustomResponse.class)))
           })
    @PostMapping("/sign-up")
    public ApiCustomResponse signUp(@Valid @RequestBody UserSignUpDto dto) {
        userService.signUp(dto);
        return ApiCustomResponse.OK("회원가입이 완료되었습니다.");
    }

    @Operation(summary = "아이디 찾기", description = "아이디 찾기 기능입니다",
               responses = {
                   @ApiResponse(responseCode = "200", description = "정상 조회",
                                content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiCustomResponse.class))),
                   @ApiResponse(responseCode = "401", description = "조회 실패",
                                content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiCustomResponse.class)))
               })
    @PostMapping("/find-id")
    public ApiCustomResponse findUserEmail(@RequestBody UserFindEmailDto dto) {
        User user = userService.findUserByPhone(dto.getUserPhone());
        return ApiCustomResponse.OK(user.getEmail(), "유저의 이메일 정보입니다.");
    }

    @Operation(summary = "비밀번호 재설정", description = "비밀번호 재설정 기능입니다. 링크와 함께 보낸 토큰과 사용자가 설정한 비밀번호를 전달해 주세요.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "정상 변경",
                                content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiCustomResponse.class))),
                   @ApiResponse(responseCode = "401", description = "변경 실패",
                                content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiCustomResponse.class)))
               })
    @PostMapping("/reset-password")
    public ApiCustomResponse resetPassword(@Valid @RequestBody ResetPasswordDto dto){
        userService.resetPassword(dto);
        return ApiCustomResponse.OK("비밀번호 설정에 성공하였습니다.");
    }

    @Operation(summary = "프로필 등록", description = "회원가입 후 프로필을 등록하는 기능입니다",
                   responses = {
                       @ApiResponse(responseCode = "200", description = "정상 등록",
                                    content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiCustomResponse.class))),
                       @ApiResponse(responseCode = "401", description = "등록 실패",
                                    content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiCustomResponse.class)))
                   })
    @PostMapping("/profile")
    public ApiCustomResponse createGroup(@AuthenticationPrincipal UserDetails userDetails,
                                         @ModelAttribute UpdateProfileDto dto) {

        if (userDetails == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }
        userService.updateProfile(userDetails.getUsername(), dto);
        return ApiCustomResponse.OK("프로필 설정에 성공하였습니다.");
    }

    @Operation(summary = "마이 페이지 메인", description = "마이페이지 메인의 정보를 전달합니다.")
    @GetMapping("/mypage")
        public ApiCustomResponse mainDetail(@AuthenticationPrincipal UserDetails userDetails) {
            if (userDetails == null) {
                        throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
                    }
            return ApiCustomResponse.OK(userService.getMainDetail(userDetails.getUsername()),"마이페이지 메인 정보입니다.");
        }

    @Operation(summary = "프로필 편집", description = "사용자의 프로필 정보를 조회.")
    @GetMapping("/detail")
    public ApiCustomResponse userDetail(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
                    throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
                }
        return ApiCustomResponse.OK(userService.getUserDetail(userDetails.getUsername()),"마이페이지 프로필 편집 정보입니다.");
    }

    @Operation(summary = "프로필 닉네임 수정", description = "사용자의 프로필 닉네임을 수정합니다.")
    @PutMapping("/profile/nickname")
    public ApiCustomResponse updateNickname(@AuthenticationPrincipal UserDetails userDetails, @RequestBody NicknameUpdateRequestDto dto){
        if (userDetails == null) {
                            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
                        }
        userService.updateNickname(userDetails.getUsername(), dto.getNickname());
        return ApiCustomResponse.OK("프로필 닉네임 수정에 성공하였습니다.");
    }

    @Operation(summary = "프로필 사진 수정", description = "사용자의 프로필 사진 수정")
    @PutMapping("/profile/image")
    public ApiCustomResponse updateUserImage(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute UserImageUpdateDto dto){
        if (userDetails == null) {
                            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
                        }
        userService.updateUserImage(userDetails.getUsername(), dto.getUserImage());
        return ApiCustomResponse.OK(userService.getUserDetail(userDetails.getUsername()),"프로필 사진 수정에 성공하였습니다.");
    }

    @Operation(summary = "현재 비밀번호 확인", description = "사용자의 현재 비밀번호를 확인합니다.")
    @PostMapping("/profile/password")
    public ApiCustomResponse verifyPassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody PasswordCheckDto passwordCheckDto){
        if (userDetails == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }
        userService.verifyPassword(userDetails.getUsername(),passwordCheckDto);
        return ApiCustomResponse.OK("비밀번호가 일치합니다.");
    }

    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
    @PutMapping("/profile/password")
    public ApiCustomResponse changePassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody PasswordCheckDto passwordCheckDto){
        if (userDetails == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }
        userService.changePassword(userDetails.getUsername(),passwordCheckDto);
        return ApiCustomResponse.OK("비밀번호 변경 성공.");
    }

    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴합니다(완전 삭제)")
    @DeleteMapping
    public ApiCustomResponse deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_ERROR);
        }
        userService.deleteByEmail(userDetails.getUsername());

        return ApiCustomResponse.OK("탈퇴 성공");
    }

}
