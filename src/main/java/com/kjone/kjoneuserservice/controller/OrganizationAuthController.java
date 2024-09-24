package com.kjone.kjoneuserservice.controller;


import com.kjone.kjoneuserservice.domain.organization.Organization_Request;
import com.kjone.kjoneuserservice.domain.organization.Organization_Response;
import com.kjone.kjoneuserservice.domain.organization_user.Organization_User;
import com.kjone.kjoneuserservice.domain.request.SignRequest;
import com.kjone.kjoneuserservice.domain.response.SignResponse;
import com.kjone.kjoneuserservice.domain.role.Authority;
import com.kjone.kjoneuserservice.domain.user.LoginRequest;
import com.kjone.kjoneuserservice.domain.user.User;
import com.kjone.kjoneuserservice.security.cookie.CookieProvider;
import com.kjone.kjoneuserservice.security.jwt.JwtProvider;
import com.kjone.kjoneuserservice.service.OrganizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/organization")
public class OrganizationAuthController {
    private final OrganizationService organizationService;
    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;

    //로그인 메서드
    @PostMapping("/signin")
    public ResponseEntity<?> Organization_signIn(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            // 사용자 인증
            Organization_Response organizationResponse = organizationService.Organization_signIn(loginRequest);
            Set<Authority> roles = organizationResponse.getRoles();
            // JWT 토큰 생성
            String token = jwtProvider.createToken(loginRequest.getEmail(), roles);
            // 쿠키에 JWT 토큰을 설정
            cookieProvider.createCookie(response, token, 3600); // 1시간

            // 로그로 쿠키 확인
            //System.out.println("Token set in cookie: " + token);

            // 로그인 성공 후 /me 엔드포인트로 리다이렉트
            return ResponseEntity.status(HttpStatus.FOUND)  // 302 Redirect
                    .header(HttpHeaders.LOCATION, "/v1/organization/me")
                    .body("로그인 되었습니다.");
        } catch (Exception e) {
            // 인증 실패 시 적절한 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)

                    .body("이메일 또는 비밀번호를 다시 확인하세요. 등록되지 않은 이메일이거나, 이메일 또는 비밀번호를 잘못 입력하셨습니다.");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody Organization_Request organizationRequest) throws Exception {
        try {
            boolean result = organizationService.Organization_signUp(organizationRequest);
            if (result) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("회원가입이 완료되었습니다.");
            } else {
                return new ResponseEntity<>("회원가입에 실패했습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    // 로그아웃 엔드포인트
    @GetMapping("/signout")
    public ResponseEntity<Void> signOut(HttpServletRequest request, HttpServletResponse response) {
        // 인증된 사용자만 로그아웃을 허용
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            System.out.println("Logging out user: " + SecurityContextHolder.getContext().getAuthentication().getName());
            cookieProvider.clearCookie(response); // 쿠키 삭제
            SecurityContextHolder.clearContext(); // Spring Security Context 클리어
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 인증되지 않은 사용자 접근 시 403 반환
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam String email) {
        try {
            organizationService.deleteOrgByEmail(email);
            return new ResponseEntity<>("사용자가 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 모든 유저 가져오기
    @GetMapping("/all")
    public ResponseEntity<List<Organization_User>> getAllUsers() {
        List<Organization_User> organizationUsers = organizationService.getAllUsers();
        return new ResponseEntity<>(organizationUsers, HttpStatus.OK);
    }
}
