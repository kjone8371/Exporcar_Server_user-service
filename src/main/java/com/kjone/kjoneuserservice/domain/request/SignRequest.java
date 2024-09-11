package com.kjone.kjoneuserservice.domain.request;


import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class SignRequest {
    private Long id;
    private String email;
    private String password;
    private String username; // 유저 이름
    private int age; // 나이
    private int phone;
    private Long image;
}

