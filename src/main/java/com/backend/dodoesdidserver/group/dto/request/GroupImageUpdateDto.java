package com.backend.dodoesdidserver.group.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class GroupImageUpdateDto {

    private Long groupId;
    private MultipartFile groupImage;

}
