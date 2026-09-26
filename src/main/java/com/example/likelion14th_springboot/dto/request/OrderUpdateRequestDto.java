package com.example.likelion14th_springboot.dto.request;

import lombok.Getter;

@Getter
public class OrderUpdateRequestDto {
    private Long memberId; // 본인 주문인지 확인용

    // 수정할 배송정보 5개
    private String receiverName;
    private String phoneNumber;
    private String roadAddress;
    private String detailAddress;
    private String zipCode;
}