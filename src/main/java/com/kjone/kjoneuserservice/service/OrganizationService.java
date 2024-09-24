package com.kjone.kjoneuserservice.service;


import com.kjone.kjoneuserservice.domain.organization.Organization_Request;
import com.kjone.kjoneuserservice.domain.organization.Organization_Response;
import com.kjone.kjoneuserservice.domain.organization_user.Organization_User;
import com.kjone.kjoneuserservice.domain.user.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface OrganizationService {
    public boolean Organization_signUp(Organization_Request organizationRequest) throws Exception;
    public Organization_Response Organization_signIn(LoginRequest loginRequest) throws Exception;
    public void logout(HttpServletResponse response);
    public Organization_User findByOrgEmail(String email);
    public void deleteOrgByEmail(String email) throws Exception; // 사용자 삭제 메서드 정의
    public List<Organization_User> getAllUsers();
    Organization_User createProfile(Long id, Organization_Request organizationRequest) throws Exception;
    Organization_User updateProfile(Long id, Organization_Request organizationRequest) throws Exception;
}
