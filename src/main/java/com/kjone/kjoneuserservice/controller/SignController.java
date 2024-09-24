package com.kjone.kjoneuserservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjone.kjoneuserservice.domain.request.SignRequest;
import com.kjone.kjoneuserservice.domain.response.SignResponse;
import com.kjone.kjoneuserservice.domain.role.Authority;
import com.kjone.kjoneuserservice.domain.user.LoginRequest;
import com.kjone.kjoneuserservice.domain.user.User;
import com.kjone.kjoneuserservice.security.cookie.CookieProvider;
import com.kjone.kjoneuserservice.security.jwt.JwtProvider;
import com.kjone.kjoneuserservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static javax.crypto.Cipher.SECRET_KEY;


// 테스트 일반 유저
@RestController
@RequestMapping("/v1/sign")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequiredArgsConstructor
public class SignController {

    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;
    private final UserService userService;


    //로그인 메서드
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            // 사용자 인증
            SignResponse signResponse = userService.signIn(loginRequest);
            Set<Authority> roles = signResponse.getRoles();
            // JWT 토큰 생성
            String token = jwtProvider.createToken(loginRequest.getEmail(), roles);
            // 쿠키에 JWT 토큰을 설정
            cookieProvider.createCookie(response, token, 3600); // 1시간

            // 로그로 쿠키 확인
            //System.out.println("Token set in cookie: " + token);

            // 로그인 성공 후 /me 엔드포인트로 리다이렉트
            return ResponseEntity.status(HttpStatus.FOUND)  // 302 Redirect
                    .header(HttpHeaders.LOCATION, "/v1/user/me")
                    .body("로그인 되었습니다.");
        } catch (Exception e) {
            // 인증 실패 시 적절한 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)

                    .body("이메일 또는 비밀번호를 다시 확인하세요. 등록되지 않은 이메일이거나, 이메일 또는 비밀번호를 잘못 입력하셨습니다.");
        }
    }


//    @PostMapping("/signin")
//    public String signIn(@RequestParam String email, @RequestParam String password, HttpServletResponse response, org.springframework.ui.Model model) {
//        try {
//            SignRequest signRequest = new SignRequest();
//            signRequest.setEmail(email);
//            signRequest.setPassword(password);
//
//            SignResponse signResponse = userService.signIn(signRequest);
//            Set<Authority> roles = signResponse.getRoles();
//
//            String token = jwtProvider.createToken(signRequest.getEmail(), roles);
//            cookieProvider.createCookie(response, token, 3600);
//
//            return "redirect:/v1/user/me";
//        } catch (Exception e) {
//            model.addAttribute("error", "이메일 또는 비밀번호를 다시 확인하세요. 등록되지 않은 이메일이거나, 이메일 또는 비밀번호를 잘못 입력하셨습니다.");
//            return "login";
//        }
//    }
//
//
//    @PostMapping("/signin")
//    public ResponseEntity<?> signIn(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
//        try {
//            // SignRequest 객체 생성 및 설정
//            SignRequest signRequest = new SignRequest();
//            signRequest.setEmail(email);
//            signRequest.setPassword(password);
//
//            // 사용자 인증
//            SignResponse signResponse = userService.signIn(signRequest);
//            Set<Authority> roles = signResponse.getRoles();
//
//            // JWT 토큰 생성
//            String token = jwtProvider.createToken(signRequest.getEmail(), roles);
//
//            // 쿠키에 JWT 토큰을 설정
//            cookieProvider.createCookie(response, token, 3600); // 1시간
//
//            // 로그로 쿠키 확인
//            System.out.println("Token set in cookie: " + token);
//
//            // 로그인 성공 후 클라이언트 측에서 인증된 페이지를 직접 요청하도록 유도
//            return ResponseEntity.ok("로그인 되었습니다. 인증된 페이지로 이동하세요.");
//
//        } catch (Exception e) {
//            // 인증 실패 시 적절한 에러 메시지 반환
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("이메일 또는 비밀번호를 다시 확인하세요. 등록되지 않은 이메일이거나, 이메일 또는 비밀번호를 잘못 입력하셨습니다.");
//        }
//    }
//
//
//     회원가입 엔드포인트
//    @PostMapping("/signup")
//    public ResponseEntity<String> signUp(@RequestBody SignRequest signRequest) throws Exception {
//
//        try {
//            boolean result = userService.signUp(signRequest);
//            if (result) {
//                return ResponseEntity.status(HttpStatus.CREATED)  // 302 Redirect
//                        .header(HttpHeaders.LOCATION, "/auth/signup")
//                        .body("회원가입이 완료되었습니다.");
////                return new ResponseEntity<>("회원가입이 완료되었습니다.", HttpStatus.CREATED);
//            } else {
//                return new ResponseEntity<>("회원가입에 실패했습니다.", HttpStatus.BAD_REQUEST);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignRequest signRequest) throws Exception {
        try {
            boolean result = userService.signUp(signRequest);
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

    // 유저 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam String email) {
        try {
            userService.deleteUserByEmail(email);
            return new ResponseEntity<>("사용자가 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 모든 유저 가져오기
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // 프로필 생성 엔드포인트
    @PostMapping("/{id}/profile")
    public ResponseEntity<User> createProfile(@PathVariable Long id, @RequestBody SignRequest signRequest) {
        try {
            User user = userService.createProfile(id, signRequest);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 프로필 업데이트 엔드포인드
    @PutMapping("/{id}/profile")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody SignRequest signRequest) {
        try {
            User user = userService.updateProfile(id, signRequest);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // JWT 테스트 엔드포인트
    @GetMapping("/test-jwt")
    public ResponseEntity<String> testJwt(@RequestParam String token) {
        boolean isValid = jwtProvider.validateToken(token);
        if (isValid) {
            return new ResponseEntity<>("JWT 토큰이 유효합니다.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("JWT 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }
    }

}
