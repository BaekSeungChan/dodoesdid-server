package com.backend.dodoesdidserver.jwt.service;

import com.backend.dodoesdidserver.common.error.ApiException;
import com.backend.dodoesdidserver.common.error.ErrorCode;
import com.backend.dodoesdidserver.jwt.JWTUtil;
import com.backend.dodoesdidserver.jwt.entity.RefreshToken;
import com.backend.dodoesdidserver.jwt.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public String reissueToken(RefreshToken refreshToken) {

        String refresh = refreshToken.getRefresh();

        if (refresh == null) {

            throw new ApiException(ErrorCode.REFRESH_TOKEN_IS_NULL_ERROR);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            throw new ApiException(ErrorCode.REFRESH_TOKEN_EXPIRED_ERROR);
        }

        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {

            throw new ApiException(ErrorCode.REFRESH_TOKEN_NOT_EXIST_ERROR);

        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        String newToken = jwtUtil.createJwt(username, role, 60 * 60 * 10L);
        String newRefresh = jwtUtil.createJwt(username, role, 3 * 24 * 60 * 60 * 1000L);

        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, newRefresh, newToken);

        return newToken;
    }

    private void addRefreshEntity(String username, String refresh, String token) {

        Date date = new Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000L);

        RefreshToken refreshEntity = RefreshToken.builder().username(username).refresh(refresh).token(token).expiration(date.toString()).build();

        refreshRepository.save(refreshEntity);
    }

}
