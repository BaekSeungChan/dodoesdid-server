package com.backend.dodoesdidserver.group.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupRequestDto {

    private String groupName;
    private MultipartFile groupImage;

}
