package com.kjone.kjoneuserservice.security.jwt;


import com.kjone.kjoneuserservice.security.cookie.CookieProvider;
import com.kjone.kjoneuserservice.security.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Setter
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String LOGIN_URI = "/auth/login";
    private static final String FAVICON_URI = "/favicon.ico";
    private static final String SIGNUP_URI = "/v1/sign/signup";
    private static final String AUTH_SIGNUP_URI = "/auth/signup";

    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;
    @Lazy
    private final CustomUserDetailService customUserDetailService;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, CookieProvider cookieProvider, CustomUserDetailService customUserDetailService) {

        this.jwtProvider = jwtProvider;
        this.cookieProvider = cookieProvider;
        this.customUserDetailService = customUserDetailService;
    }


//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String requestURI = request.getRequestURI();
//
//        // URI 출력 로그
//        System.out.println("Requested URI: " + request.getRequestURI());
//
//        // 로그인 페이지와 favicon 요청을 필터에서 제외
//        if (requestURI.equals("/auth/login") || requestURI.equals("/favicon.ico")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//        String jwt = cookieProvider.resolveToken(request);
//        if (jwt != null && jwtProvider.validateToken(jwt)) {
//            String username = jwtProvider.getAccount(jwt);
//            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
//            if (userDetails != null) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        logger.debug("Requested URI: {}", requestURI);

        if (isPublicPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = cookieProvider.resolveToken(request);
        if (token != null) {
            processToken(token, request);
        } else {
            logger.debug("No token found");
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String requestURI) {
        return LOGIN_URI.equals(requestURI) || FAVICON_URI.equals(requestURI) ||
                SIGNUP_URI.equals(requestURI) || AUTH_SIGNUP_URI.equals(requestURI);
    }

    private void processToken(String token, HttpServletRequest request) {
        logger.debug("Token received: {}", token);
        try {
            if (jwtProvider.validateToken(token)) {
                String username = jwtProvider.getAccount(token);
                logger.debug("Extracted username from token: {}", username);

                UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
                if (userDetails != null) {
                    setAuthentication(userDetails, request);
                    logger.debug("User authenticated: {}", username);
                } else {
                    logger.debug("User details not found for username: {}", username);
                }
            } else {
                logger.debug("Invalid token");
            }
        } catch (Exception e) {
            logger.error("Error parsing token: {}", e.getMessage(), e);
        }
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }



    private String resolveTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);  // "Bearer " 제거
        }
        return null;
    }

}
