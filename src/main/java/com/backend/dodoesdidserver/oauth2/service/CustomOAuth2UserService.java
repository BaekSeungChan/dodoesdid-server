package com.backend.dodoesdidserver.oauth2.service;

import com.backend.dodoesdidserver.oauth2.dto.GoogleResponseDto;
import com.backend.dodoesdidserver.oauth2.dto.KakaoResponseDto;
import com.backend.dodoesdidserver.oauth2.dto.OAuth2ResponseDto;
import com.backend.dodoesdidserver.oauth2.entity.CustomOAuth2User;
import com.backend.dodoesdidserver.user.entity.User;
import com.backend.dodoesdidserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
       public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

           OAuth2User oAuth2User = super.loadUser(userRequest);

           String registrationId = userRequest.getClientRegistration().getRegistrationId();
           OAuth2ResponseDto oAuth2Response;
           if (registrationId.equals("kakao")) {

               oAuth2Response = new KakaoResponseDto(oAuth2User.getAttributes());
               System.err.println(oAuth2Response);
           }
           else if (registrationId.equals("google")) {

               oAuth2Response = new GoogleResponseDto(oAuth2User.getAttributes());
               System.err.println(oAuth2Response);
           }
           else {

               return null;
           }
        System.err.println(oAuth2Response.getEmail());

           String username = oAuth2Response.getEmail();
           User existData = userRepository.findByEmail(username);

           if (existData == null) {

               User user = User.builder().email(username).role("user").name(oAuth2Response.getName()).build();
               userRepository.save(user);

               return new CustomOAuth2User(user);
           }
           else {

               return new CustomOAuth2User(existData);
           }
       }
}
