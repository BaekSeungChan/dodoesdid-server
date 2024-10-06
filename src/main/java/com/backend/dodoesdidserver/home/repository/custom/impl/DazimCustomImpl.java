package com.backend.dodoesdidserver.home.service.custom.impl;


import com.backend.dodoesdidserver.home.repository.custom.DazimCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DazimCustomImpl implements DazimCustom {

    private final JPAQueryFactory jpaQueryFactory;


}
