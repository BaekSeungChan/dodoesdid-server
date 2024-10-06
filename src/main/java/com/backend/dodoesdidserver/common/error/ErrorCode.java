package com.backend.dodoesdidserver.common.error;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // User 관련 에러 코드는 1000번대 사용
    EMAIL_ALREADY_REGISTERED_ERROR(HttpStatus.BAD_REQUEST.value(), 1400, "이미 가입된 이메일입니다."),
    PHONE_NOT_REGISTERED_ERROR(HttpStatus.BAD_REQUEST.value(), 1401, "등록되지 않은 전화번호입니다."),
    EMAIL_NOT_EXIST_ERROR(HttpStatus.BAD_REQUEST.value(), 1402, "등록되지 않은 이메일입니다."),
    TOKEN_NOT_EXIST_ERROR(HttpStatus.BAD_REQUEST.value(), 1403, "인증 토큰이 만료되었거나 없는 유저입니다."),
    PHONE_ALREADY_REGISTERED_ERROR(HttpStatus.BAD_REQUEST.value(), 1404, "이미 가입한 전화번호 입니다."),
    USER_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST.value(), 1405, "로그인이 해주세요."),
    UNAUTHORIZED_ACCESS(HttpStatus.BAD_REQUEST.value(), 1406, "권한이 없는 사용자입니다."),
    NICKNAME_ALREADY_EXIST_ERROR(HttpStatus.BAD_REQUEST.value(), 1407, "이미 존재하는 닉네임 입니다."),
    NO_CHANGE_INFORMATION_ERROR(HttpStatus.BAD_REQUEST.value(), 1408, "변경 사항이 없습니다."),
    PASSWORD_NOT_MATCH_ERROR(HttpStatus.BAD_REQUEST.value(), 1409, "비밀번호가 일치하지 않습니다."),

    // Image 관련 에러 코드는 2000번대 사용
    EMPTY_FILE_EXCEPTION(HttpStatus.BAD_REQUEST.value(), 2400, "빈 파일 입니다."),
    NO_FILE_EXTENSION(HttpStatus.BAD_REQUEST.value(), 2401, "확장자를 찾을 수 없는 경우"),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST.value(), 2402, "잘못된 확장자입니다. jpg, jpeg, png, gif 파일만 가능합니다."),
    PUT_OBJECT_EXCEPTION(HttpStatus.BAD_REQUEST.value(),2403 ,"S3파일 업로드 실패"),
    IO_EXCEPTION_ON_IMAGE_UPLOAD(HttpStatus.BAD_REQUEST.value(), 2404, "이미지 업로드 중 IO 작업 실패"),

    // Email 에러 코드는 3000번대 사용
    VERIFICATION_EMAIL_NOT_EXIST_ERROR(HttpStatus.BAD_REQUEST.value(), 3400, "인증하려는 이메일이 존재하지 않습니다."),
    VERIFICATION_CODE_NOT_MATCH_ERROR(HttpStatus.BAD_REQUEST.value(), 3401, "인증번호가 일치하지 않습니다."),

    // Group 에러 코드는 4000번대 사용
    GROUP_NOT_FOUND(HttpStatus.BAD_REQUEST.value(),4400 , "그룹 이름은 필수입니다."),
    GROUP_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(),4401 , "이미 존재하는 그룹이름입니다."),
    USER_ALREADY_IN_GROUP(HttpStatus.BAD_REQUEST.value(),4402 , "이미 그룹에 가입된 사용자입니다."),


    // 토큰 관련 에러 코드는 5000번대 사용
    REFRESH_TOKEN_NOT_EXIST_ERROR(HttpStatus.BAD_REQUEST.value(),5400 , "리프레시 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_EXPIRED_ERROR(HttpStatus.BAD_REQUEST.value(),5401 , "리프레시 토큰이 만료되었습니다."),
    REFRESH_TOKEN_IS_NULL_ERROR(HttpStatus.BAD_REQUEST.value(),5402 , "리프레시 토큰이 빈 값입니다."),
    ACCESS_TOKEN_IS_EXPIRED_ERROR(HttpStatus.BAD_REQUEST.value(),5403 , "액세스 토큰이 만료되었습니다."),
    GROUP_USER_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST.value(), 4402, "그룹 멤버가 아닙니다."),

    // Dazim 에러 코드는 6000번대 사용
    DAZIM_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST.value(), 6400, "존재하지 않는 다짐입니다."),
    GROUP_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST.value(), 6401, "그룹이 존재하지 않습니다."),
    INVALID_BUTTON_NUMBER(HttpStatus.BAD_REQUEST.value(), 6402, "존재하지 이모지 입니다."),
    COMMENT_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST.value(), 6403, "다짐 댓글이 존재하지 않습니다."),
    REPLY_COMMENT_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST.value(), 6404, "대댓글이 존재하지 않습니다."),


    INVALID_DATE_ERROR(HttpStatus.BAD_REQUEST.value(), 7400, "알맞은 DATE 형식이 아닙니다.");


    private int httpStatusCode;
    private int code;
    private String message;

}
