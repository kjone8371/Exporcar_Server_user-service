package com.kjone.kjoneuserservice.domain.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kjone.kjoneuserservice.domain.role.Authority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_entity")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String email;
    private String password;
    private String username; // 유저 이름
    private int age; // 나이
    private int phone; // 전화번호

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<UserFile> userFiles;

    @JsonIgnore
    private Long image;

    @CreationTimestamp // INSERT 시 자동으로 값을 채워줌
    private LocalDateTime createTime = LocalDateTime.now();

    @UpdateTimestamp // UPDATE 시 자동으로 값을 채워줌
    private LocalDateTime updateTime = LocalDateTime.now();

    // 권한이라는 것에 외래키를 추가 함으로써
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "authority", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Authority> roles = new HashSet<>();


    public User(Long id, String email, String password, String username, int age, int phone, LocalDateTime createTime, LocalDateTime updateTime, Set<Authority> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.age = age;
        this.phone = phone;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.roles = roles;
    }
}

