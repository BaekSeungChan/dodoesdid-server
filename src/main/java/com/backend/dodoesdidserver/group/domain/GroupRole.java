package com.backend.dodoesdidserver.group.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GroupRole {

    ADMIN("그룹장"),
    MEMBER("그룹원");

    private final String text;
}
