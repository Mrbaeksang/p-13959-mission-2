package com.mysite.sbb.user;// com.mysite.sbb.user.CustomOAuth2UserService.java

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService; // SiteUser 관리 서비스

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, kakao 등
        String providerId = oAuth2User.getName(); // 구글의 경우 sub(고유ID)
        String email = oAuth2User.getAttribute("email"); // 구글의 경우 email 속성

        // 1. DB에 SiteUser가 있으면 return, 없으면 자동 생성
        SiteUser user = userService.getUserByEmail(email);
        if (user == null) {
            user = userService.createOAuthUser(email, provider, providerId); // 신규 생성
        }
        // 권한 등 추가 처리 필요시 여기서
        return oAuth2User; // (여기서 user정보를 래핑해서 반환해도 됨)
    }
}
