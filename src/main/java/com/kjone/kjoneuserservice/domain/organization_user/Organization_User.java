package com.kjone.kjoneuserservice.domain.organization_user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kjone.kjoneuserservice.domain.role.Authority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "organization_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization_User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String email;
    private String password;
    private String name;
    private int age;
    private int phone;

    @JsonIgnore
    private Long image;

    @CreationTimestamp // INSERT 시 자동으로 값을 채워줌
    private LocalDateTime createTime = LocalDateTime.now();

    @UpdateTimestamp // UPDATE 시 자동으로 값을 채워줌
    private LocalDateTime updateTime = LocalDateTime.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "authority", joinColumns = @JoinColumn(name = "organization_id"))
    @Column(name = "role")
    private Set<Authority> roles = new HashSet<>();

    public Organization_User(Long id, String email, String password, String name, int age, int phone, LocalDateTime createTime, LocalDateTime updateTime, Set<Authority> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.roles = roles;
    }
}
