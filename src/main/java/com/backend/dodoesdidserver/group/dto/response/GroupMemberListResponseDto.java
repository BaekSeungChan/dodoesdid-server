package com.backend.dodoesdidserver.group.dto.response;

import com.backend.dodoesdidserver.group.domain.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberListResponseDto {

    private Long userId;
    private Long groupId;
    private String userName;
    private GroupRole groupRole;

}
