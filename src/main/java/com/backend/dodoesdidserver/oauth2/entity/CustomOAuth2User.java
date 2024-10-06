package com.backend.dodoesdidserver.oauth2.entity;

import com.backend.dodoesdidserver.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private final User user;

        public CustomOAuth2User(User oAuth2User) {

            this.user = oAuth2User;
        }

        @Override
        public Map<String, Object> getAttributes() {

            return null;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {

            Collection<GrantedAuthority> collection = new ArrayList<>();

            collection.add((GrantedAuthority) user::getRole);

            return collection;
        }

        @Override
        public String getName() {

            return user.getName();
        }

        public String getEmail() {

            return user.getEmail();
        }
}
