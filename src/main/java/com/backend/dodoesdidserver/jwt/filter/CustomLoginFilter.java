package com.backend.dodoesdidserver.jwt.filter;

import com.backend.dodoesdidserver.jwt.JWTUtil;
import com.backend.dodoesdidserver.jwt.entity.RefreshToken;
import com.backend.dodoesdidserver.jwt.repository.RefreshRepository;
import com.backend.dodoesdidserver.user.dto.UserLoginDto;
import com.backend.dodoesdidserver.user.entity.CustomUserDetails;
import com.backend.dodoesdidserver.user.entity.User;
import com.backend.dodoesdidserver.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserLoginDto dto = new ObjectMapper().readValue(request.getInputStream(), UserLoginDto.class);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword(), null);
            authToken.setDetails(dto.isRemember());
            return authenticationManager.authenticate(authToken);
        }catch (IOException e) {
            System.out.println("Failed to parse authentication request body: " + e.getMessage());
            throw new AuthenticationServiceException("Failed to parse authentication request body", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        String token = jwtUtil.createJwt(username, role, 60 * 60 * 1000L);

        boolean remember = (Boolean) authentication.getDetails();
        if(remember) {
            String refresh = jwtUtil.createJwt(username, role, 3 * 24 * 60 * 60 * 1000L);
            addRefreshEntity(username,refresh,token);
        }

        User user = userRepository.findByEmail(username);

        boolean result = false;

        if(user.getNickname() != null){
            result = true;
        }

        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("userNickName", result);

        response.setContentType("application/json");
        response.addHeader("Authorization", "Bearer " + token);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(userInfo));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }

    private void addRefreshEntity(String username, String refresh, String token) {

        Date date = new Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000L);

        RefreshToken refreshEntity = RefreshToken.builder().username(username).refresh(refresh).token(token).expiration(date.toString()).build();

        refreshRepository.save(refreshEntity);
    }
}
