package com.example.likelion14th_springboot.controller; // 본인 어플 이름에 맞게 변경하기

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/api/health")
    public String health() {
        return "야호~";
    }
}