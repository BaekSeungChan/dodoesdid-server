package com.backend.dodoesdidserver.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateProfileDto {

    private MultipartFile image;
    private String nickname;

}
