package com.example.likelion14th_springboot.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Member
    DUPLICATE_MEMBER_NAME(HttpStatus.CONFLICT, "MEMBER_001", "이미 사용 중인 이름입니다.");

    private final HttpStatus status; // HTTP 상태코드
    private final String code;       // 클라이언트가 분기 처리에 사용할 에러 코드
    private final String message;    // 사용자에게 보여줄 메시지
}