package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.mail.MailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    // 일반 회원가입용
    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    /**
     * 사용자 조회: username, email, providerId(소셜로그인) 모두 지원
     * @param idOrUsernameOrEmail - username, email, 또는 소셜 providerId
     * @return SiteUser
     * @throws DataNotFoundException 못 찾으면 예외
     */
    public SiteUser getUser(String idOrUsernameOrEmail) {
        // 1. username으로 우선 조회 (내부 로그인/소셜 providerId 모두 고려)
        Optional<SiteUser> userOpt = this.userRepository.findByUsername(idOrUsernameOrEmail);
        if (userOpt.isPresent()) return userOpt.get();

        // 2. 이메일로도 조회 (혹시 이메일로 접근할 경우)
        userOpt = this.userRepository.findByEmail(idOrUsernameOrEmail);
        if (userOpt.isPresent()) return userOpt.get();

        // 3. providerId 기준(여기선 구글만 예시, 필요시 provider를 매개변수로)
        userOpt = this.userRepository.findByProviderId(idOrUsernameOrEmail);
        if (userOpt.isPresent()) return userOpt.get();

        throw new DataNotFoundException("사용자를 찾을 수 없습니다.");
    }

    // 임시 비밀번호 생성(8~12자리)
    public String createTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        int len = rnd.nextInt(5) + 8; // 8~12자리
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // 비밀번호 초기화(이메일 or 아이디로)
    @Transactional
    public void resetPassword(String usernameOrEmail) {
        SiteUser user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new DataNotFoundException("사용자를 찾을 수 없습니다.")));

        String tempPassword = createTempPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        mailService.sendMail(
                user.getEmail(),
                "[SBB] 임시 비밀번호 안내",
                "임시 비밀번호: " + tempPassword + "\n로그인 후 반드시 비밀번호를 변경해 주세요."
        );
    }

    // 비밀번호 변경
    public void changePassword(SiteUser user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 이메일로 사용자 찾기
    public SiteUser getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * 소셜(OAuth) 로그인 최초 가입 처리
     * @param email 이메일
     * @param provider 소셜제공자 (google, kakao 등)
     * @param providerId 소셜 고유 ID(sub)
     */
    public SiteUser createOAuthUser(String email, String provider, String providerId) {
        SiteUser user = new SiteUser();
        user.setUsername(providerId);      // 소셜 계정의 sub(고유값)을 username으로 저장(중복 방지)
        user.setEmail(email);
        user.setProvider(provider);        // "google", "kakao" 등
        user.setProviderId(providerId);    // 구글 sub, 카카오 id 등
        userRepository.save(user);
        return user;
    }

    // (추가) providerId로 사용자 찾기 - Repository에 메서드 추가 필요
    public SiteUser getUserByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new DataNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
