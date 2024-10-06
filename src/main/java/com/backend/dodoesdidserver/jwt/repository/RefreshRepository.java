package com.backend.dodoesdidserver.jwt.repository;

import com.backend.dodoesdidserver.jwt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByRefresh(String refresh);
    Boolean existsByToken(String token);
    RefreshToken findFirstByTokenOrderByExpirationDesc(String token);
    @Transactional
    void deleteByRefresh(String refresh);
    @Transactional
    void deleteByToken(String token);
}
