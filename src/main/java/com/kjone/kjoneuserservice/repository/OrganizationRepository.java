package com.kjone.kjoneuserservice.repository;

import com.kjone.kjoneuserservice.domain.organization_user.Organization_User;
import com.kjone.kjoneuserservice.domain.user.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization_User,Long> {
    Optional<Organization_User> findByEmail(String email);

    Optional<Organization_User> findById(Long id);
}
