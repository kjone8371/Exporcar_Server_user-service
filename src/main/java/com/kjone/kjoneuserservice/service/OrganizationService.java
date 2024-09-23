package com.kjone.kjoneuserservice.service;


import com.kjone.kjoneuserservice.domain.request.SignRequest;
import com.kjone.kjoneuserservice.domain.response.SignResponse;
import com.kjone.kjoneuserservice.domain.user.LoginRequest;
import com.kjone.kjoneuserservice.domain.user.User;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface OrganizationService {
    public boolean Organization_signUp(SignRequest signRequest) throws Exception;
    public SignResponse Organization_signIn(LoginRequest loginRequest) throws Exception;
    public void logout(HttpServletResponse response);
    public User findByEmail(String email);
    public void deleteUserByEmail(String email) throws Exception; // 사용자 삭제 메서드 정의
    public List<User> getAllUsers();
    User createProfile(Long userId, SignRequest signRequest) throws Exception;
    User updateProfile(Long userId, SignRequest signRequest) throws Exception;
}
