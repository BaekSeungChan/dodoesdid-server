package com.backend.dodoesdidserver.home.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DazimSucess {

    SUCESS("성공"),
    FAIL("실패");

    private final String test;
}
