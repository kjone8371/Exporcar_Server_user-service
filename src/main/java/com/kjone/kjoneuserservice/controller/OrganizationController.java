package com.kjone.kjoneuserservice.controller;


import com.kjone.kjoneuserservice.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// 조직도 생성/관리
@RestController
@RequestMapping("/v1/organization")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;



}
