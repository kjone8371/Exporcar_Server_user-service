package com.kjone.kjoneuserservice.domain.organization;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kjone.kjoneuserservice.domain.organization_user.Organization_User;
import com.kjone.kjoneuserservice.domain.role.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Organization_Response {
    private Long id;
    private String email;
    private String password;
    private String name;
    private int age;
    private int phone;
    private Long image;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Set<Authority> roles;

    public Organization_Response(Organization_User organization_user) {
        this.id = organization_user.getId();
        this.email = organization_user.getEmail();
        this.password = organization_user.getPassword();
        this.name = organization_user.getName();
        this.age = organization_user.getAge();
        this.phone = organization_user.getPhone();
        this.image = organization_user.getImage();
        this.createTime = organization_user.getCreateTime();
        this.updateTime = organization_user.getUpdateTime();
        this.roles = organization_user.getRoles();
    }
}
