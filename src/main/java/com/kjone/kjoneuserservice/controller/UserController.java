package com.kjone.kjoneuserservice.controller;


import com.kjone.kjoneuserservice.security.cookie.CookieProvider;
import com.kjone.kjoneuserservice.security.jwt.JwtProvider;
import com.kjone.kjoneuserservice.security.service.CustomUserDetailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;
    private final CustomUserDetailService userDetailService;

    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        // 쿠키에서 JWT 추출
        String jwt = cookieProvider.resolveToken(request);

        if (jwt != null && jwtProvider.validateToken(jwt)) {
            // JWT에서 사용자 이메일 추출
            String username = jwtProvider.getAccount(jwt);
            // 사용자 정보 가져오기
            UserDetails userDetails = userDetailService.loadUserByUsername(username);
            if (userDetails != null) {
                return ResponseEntity.ok(userDetails);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
    }
}
