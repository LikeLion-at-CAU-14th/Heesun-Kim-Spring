package com.example.likelion14th_springboot.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable // 별도의 테이블 x (Orders 테이블 안에 컬럼 5개로 풀려서 저장)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {
    private String receiverName;  // 수령인
    private String phoneNumber;   // 전화번호
    private String roadAddress;   // 도로명주소
    private String detailAddress; // 상세주소
    private String zipCode;       // 우편번호
}