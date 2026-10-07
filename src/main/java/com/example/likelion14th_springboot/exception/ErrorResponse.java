package com.example.likelion14th_springboot.exception;

import java.time.LocalDateTime;

// 클라이언트에게 내려줄 에러 응답 포맷
public record ErrorResponse(
        int status,              // HTTP 상태코드 숫자
        String code,             // 에러 코드
        String message,          // 에러 메시지
        LocalDateTime timestamp  // 에러가 발생한 시각
) {
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage(),
                LocalDateTime.now()
        );
    }
}