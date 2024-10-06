package com.backend.dodoesdidserver.group.dto.request;

import lombok.Getter;

@Getter
public class ChangeAdminRequestDto {

    private Long nextAdminId;
    private Long groupId;

}
