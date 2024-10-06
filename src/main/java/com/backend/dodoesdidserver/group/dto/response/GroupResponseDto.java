package com.backend.dodoesdidserver.group.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupResponseDto {

    private String groupName;
    private String groupUrl;

    public static GroupResponseDto toGroupDto(String groupName, String groupUrl) {
        return GroupResponseDto.builder()
                .groupName(groupName)
                .groupUrl(groupUrl)
                .build();
    }

}
