package com.backend.dodoesdidserver.user.dto;

import lombok.Getter;

@Getter
public class UserLoginDto {
    private String username;
    private String password;

    @Getter
    private boolean remember;

}
