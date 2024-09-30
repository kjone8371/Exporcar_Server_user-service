package com.kjone.kjoneuserservice.service.impl;


import com.kjone.kjoneuserservice.domain.request.SignRequest;
import com.kjone.kjoneuserservice.domain.response.SignResponse;
import com.kjone.kjoneuserservice.domain.role.Authority;
import com.kjone.kjoneuserservice.domain.user.LoginRequest;
import com.kjone.kjoneuserservice.domain.user.User;
import com.kjone.kjoneuserservice.repository.UserRepository;
import com.kjone.kjoneuserservice.security.cookie.CookieProvider;
import com.kjone.kjoneuserservice.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CookieProvider cookieProvider;

    @Override
    @Transactional
    public boolean signUp(SignRequest request) throws Exception{
        try {

            // 이메일 중복 확인
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                throw new Exception("이미 사용 중인 이메일입니다.");
            }
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .username(request.getUsername())
                    .age(request.getAge())
                    .phone(request.getPhone())
                    .image(request.getImage())
                    .roles(Collections.singleton(Authority.USER)) // roles 필드를 설정합니다.
                    .build();
            userRepository.save(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("이미 사용 중인 이메일 입니다.");
        }
        return true;
    }

    // 로그인 메서드
    @Override
    public SignResponse signIn(LoginRequest loginRequest) throws Exception {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return new SignResponse(user);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }


    @Override
    public void logout(HttpServletResponse response) {
        Cookie access = cookieProvider.of(cookieProvider.removeAccessTokenCookie());
        Cookie refresh = cookieProvider.of(cookieProvider.removeRefreshTokenCookie());

        response.addCookie(access);
        response.addCookie(refresh);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null); // 사용자 없을 경우 null 반환
    }

    @Override
    @Transactional
    public void deleteUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll().stream()
                .map(user -> new User(
                        user.getId(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getUsername(),
                        user.getAge(),
                        user.getPhone(),
                        user.getCreateTime(),
                        user.getUpdateTime(),
                        user.getRoles()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public User createProfile(Long userId, SignRequest signRequest) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

        user.setUsername(signRequest.getUsername());
        user.setAge(signRequest.getAge());
        user.setImage(signRequest.getImage());
        user.setUpdateTime(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, SignRequest signRequest) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        user.setUsername(signRequest.getUsername());
        user.setAge(signRequest.getAge());
        user.setImage(signRequest.getImage());
        user.setUpdateTime(LocalDateTime.now());

        return userRepository.save(user);
    }
}