package com.example.likelion14th_springboot.dto.request;

import lombok.Getter;

@Getter
public class OrderItemRequestDto {
    private Long productId;   // 주문할 상품 ID
    private Integer quantity; // 주문 수량
}