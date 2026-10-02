package com.example.likelion14th_springboot.dto.response;

import com.example.likelion14th_springboot.domain.Member;

// 회원가입 성공 응답
public record JoinResponseDto(
        Long memberId,
        String name
) {
    public static JoinResponseDto fromEntity(Member member) {
        return new JoinResponseDto(member.getId(), member.getName());
    }
}