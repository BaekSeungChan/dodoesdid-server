package com.backend.dodoesdidserver.user.service;

import com.backend.dodoesdidserver.user.entity.CustomUserDetails;
import com.backend.dodoesdidserver.user.entity.User;
import com.backend.dodoesdidserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userData = userRepository.findByEmail(username);

        if (userData != null) {
            return new CustomUserDetails(userData);
        }

        return null;
    }
}
