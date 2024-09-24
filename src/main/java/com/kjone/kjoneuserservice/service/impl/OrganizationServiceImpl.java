package com.kjone.kjoneuserservice.service.impl;


import com.kjone.kjoneuserservice.domain.organization.Organization_Request;
import com.kjone.kjoneuserservice.domain.organization.Organization_Response;
import com.kjone.kjoneuserservice.domain.organization_user.Organization_User;
import com.kjone.kjoneuserservice.domain.request.SignRequest;
import com.kjone.kjoneuserservice.domain.role.Authority;
import com.kjone.kjoneuserservice.domain.user.LoginRequest;
import com.kjone.kjoneuserservice.repository.OrganizationRepository;
import com.kjone.kjoneuserservice.security.cookie.CookieProvider;
import com.kjone.kjoneuserservice.service.OrganizationService;
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
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final CookieProvider cookieProvider;

    @Override
    @Transactional
    public boolean Organization_signUp(Organization_Request organizationRequest) throws Exception{
        try {

            // 이메일 중복 확인
            Optional<Organization_User> existingUser = organizationRepository.findByEmail(organizationRequest.getEmail());
            if (existingUser.isPresent()) {
                throw new Exception("이미 사용 중인 이메일입니다.");
            }
            Organization_User organization_user = Organization_User.builder()
                    .email(organizationRequest.getEmail())
                    .password(passwordEncoder.encode(organizationRequest.getPassword()))
                    .name(organizationRequest.getName())
                    .age(organizationRequest.getAge())
                    .phone(organizationRequest.getPhone())
                    .image(organizationRequest.getImage())
                    .roles(Collections.singleton(Authority.USER)) // roles 필드를 설정합니다.
                    .build();
            organizationRepository.save(organization_user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("이미 사용 중인 이메일 입니다.");
        }
        return true;
    }

    // 로그인 메서드
    @Override
    public Organization_Response Organization_signIn(LoginRequest loginRequest) throws Exception {
        Organization_User organization_user = organizationRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (passwordEncoder.matches(loginRequest.getPassword(), organization_user.getPassword())) {
            return new Organization_Response(organization_user);
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
    public Organization_User findByOrgEmail(String email) {
        return organizationRepository.findByEmail(email).orElse(null); // 사용자 없을 경우 null 반환
    }

    @Override
    @Transactional
    public void deleteOrgByEmail(String email) throws Exception {
        Organization_User organization_user = organizationRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));
        organizationRepository.delete(organization_user);
    }

    @Override
    public List<Organization_User> getAllUsers() {

        return organizationRepository.findAll().stream()
                .map(organization_user -> new Organization_User(
                        organization_user.getId(),
                        organization_user.getEmail(),
                        organization_user.getPassword(),
                        organization_user.getName(),
                        organization_user.getAge(),
                        organization_user.getPhone(),
                        organization_user.getCreateTime(),
                        organization_user.getUpdateTime(),
                        organization_user.getRoles()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Organization_User createProfile(Long id, Organization_Request organizationRequest) throws Exception {
        Organization_User organization_user = organizationRepository.findById(id)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

        organization_user.setName(organizationRequest.getName());
        organization_user.setAge(organizationRequest.getAge());
        organization_user.setImage(organizationRequest.getImage());
        organization_user.setUpdateTime(LocalDateTime.now());

        return organizationRepository.save(organization_user);
    }

    @Override
    @Transactional
    public Organization_User updateProfile(Long id, Organization_Request organizationRequest) throws Exception {
        Organization_User organization_user = organizationRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        organization_user.setName(organizationRequest.getName());
        organization_user.setAge(organizationRequest.getAge());
        organization_user.setImage(organizationRequest.getImage());
        organization_user.setUpdateTime(LocalDateTime.now());

        return organizationRepository.save(organization_user);
    }
}
