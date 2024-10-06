package com.backend.dodoesdidserver.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPageDetailResponseDto {

    private String userNickname;
    private String userImage;

}
