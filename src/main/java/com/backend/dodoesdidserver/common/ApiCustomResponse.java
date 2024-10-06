package com.backend.dodoesdidserver.common;


import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiCustomResponse<T> {

    private int code;
    private String message;
    private T data;

    @Builder
    private ApiCustomResponse(int code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiCustomResponse<T> OK(T data){
        return ApiCustomResponse.<T>builder()
                .code(200)
                .message("성공")
                .data(data)
                .build();
    }

    public static <T> ApiCustomResponse<T> OK(String message){
            return ApiCustomResponse.<T>builder()
                    .code(200)
                    .message(message)
                    .build();
    }

    public static <T> ApiCustomResponse<T> OK(T data, String message){
        return ApiCustomResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    public static ApiCustomResponse ERROR(Exception e){
        return ApiCustomResponse.builder()
                .code(500)
                .message(e.getLocalizedMessage())
                .build();
    }

    public static ApiCustomResponse Error(ErrorCode errorCode){
        return ApiCustomResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    public static ApiCustomResponse ERROR(int code, String message) {
        return ApiCustomResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static ApiCustomResponse ERROR(String message) {
        return ApiCustomResponse.builder()
                .message(message)
                .build();
    }

}