package com.kjone.kjoneuserservice.domain.request;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProfileRequest {

    private String nickname;
    private Long post;
    private int Flower;
    private int Flowing;
    private String avatar;
    private String signature;

}
