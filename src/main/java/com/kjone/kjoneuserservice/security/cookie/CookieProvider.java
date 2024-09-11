package com.kjone.kjoneuserservice.security.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieProvider {

    private static final String COOKIE_NAME = "JWT_TOKEN";
    private static final int COOKIE_MAX_AGE = 60 * 60; // 1 Hour

    public void createCookie(HttpServletResponse response, String token, int maxAge) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setPath("/"); // 쿠키의 경로 설정
        cookie.setHttpOnly(true); // JavaScript에서 접근 불가
        cookie.setMaxAge(maxAge); // 쿠키의 유효 시간 설정
        response.addCookie(cookie);
    }

//    public void createCookie(HttpServletResponse response, String token, int maxAge) {
//        Cookie cookie = new Cookie("JWT_TOKEN", token);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // Only send cookie over HTTPS
//        cookie.setPath("/");
//        cookie.setMaxAge(3600); // 1 hour
//        cookie.setSecure(true); // CSRF 보호
//        response.addCookie(cookie);
//
//    }

//    public void setCookie(HttpServletResponse response) {
//        Cookie cookie = new Cookie("ILOGIN", "Y");
//        cookie.setDomain(".coupang.com");
//        cookie.setPath("/");
//        cookie.setMaxAge(3600); // 쿠키의 만료 시간을 1시간으로 설정
//        cookie.setSecure(true); // HTTPS 연결에서만 쿠키 전송
//        cookie.setHttpOnly(true); // JavaScript에서 접근 불가
//        response.addCookie(cookie);
//    }


    public String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    public void clearCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public ResponseCookie removeRefreshTokenCookie() {
        return ResponseCookie.from("refresh-token", null)
                .maxAge(0)
                .path("/")
                .build();
    }
    public ResponseCookie removeAccessTokenCookie() {
        return ResponseCookie.from("access-token", null)
                .maxAge(0)
                .path("/")
                .build();
    }

    public Cookie of(ResponseCookie responseCookie) {
        Cookie cookie = new Cookie(responseCookie.getName(), responseCookie.getValue());
        cookie.setPath(responseCookie.getPath());
        cookie.setSecure(responseCookie.isSecure());
        cookie.setHttpOnly(responseCookie.isHttpOnly());
        cookie.setMaxAge((int) responseCookie.getMaxAge().getSeconds());
        return cookie;
    }
}

