package com.mysite.sbb.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    // ====== [추가] 소셜 로그인 제공자 정보 ======
    private String provider;      // ex) google, kakao 등
    private String providerId;    // 소셜에서 주는 고유 ID
}
