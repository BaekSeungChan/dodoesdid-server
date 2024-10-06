package com.backend.dodoesdidserver.jwt.filter;

import com.backend.dodoesdidserver.jwt.JWTUtil;
import com.backend.dodoesdidserver.jwt.entity.RefreshToken;
import com.backend.dodoesdidserver.jwt.repository.RefreshRepository;
import com.backend.dodoesdidserver.jwt.service.RefreshTokenService;
import com.backend.dodoesdidserver.user.entity.CustomUserDetails;
import com.backend.dodoesdidserver.user.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        if (requestUri.matches("^\\/login(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }
        if (requestUri.matches("^\\/oauth2(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = null;
        Cookie[] cookies = request.getCookies();
        String authorization = request.getHeader("Authorization");

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Set-Cookie".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }else if(authorization != null && authorization.startsWith("Bearer ")) {

            token = authorization.split(" ")[1];

        }else{
            filterChain.doFilter(request, response);

            return;
        }

        try {
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            RefreshToken refresh = refreshRepository.findFirstByTokenOrderByExpirationDesc(token);
            if(refresh != null) {
                token = refreshTokenService.reissueToken(refresh);
                System.err.println(token);
                response.addHeader("Authorization", "Bearer " + token);
            }
        }

        if (jwtUtil.isExpired(token)) {

            filterChain.doFilter(request, response);

            return;
        }

        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().email(username).role(role).build());

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
