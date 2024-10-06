package com.backend.dodoesdidserver.group.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserGroupListResponseDto {

    private long groupId;
    private String groupImage;
    private String groupName;

}
