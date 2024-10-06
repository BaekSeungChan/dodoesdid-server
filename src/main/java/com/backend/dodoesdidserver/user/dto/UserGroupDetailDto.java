package com.backend.dodoesdidserver.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGroupDetailDto {

    private long groupId;
    private String groupName;
    private String groupImage;
    private String groupNotice;
    private String groupUrl;

}
