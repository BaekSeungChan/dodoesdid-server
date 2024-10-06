package com.backend.dodoesdidserver.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailResponseDto {

    private String userNickname;
    private String userImage;
    private String userEmail;

}
