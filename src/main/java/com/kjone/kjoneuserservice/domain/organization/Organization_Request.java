package com.kjone.kjoneuserservice.domain.organization;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Organization_Request {
    private Long id;
    private String email;
    private String password;
    private String name;
    private int age;
    private int phone;
    private Long image;
}
