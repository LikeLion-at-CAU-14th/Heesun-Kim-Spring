package com.example.likelion14th_springboot.dto.request;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Orders;
import com.example.likelion14th_springboot.domain.ShippingAddress;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreateRequestDto {
    private Long memberId; // 구매자 ID
    private List<OrderItemRequestDto> items; // 주문 상품 목록

    // 배송정보 5개
    private String receiverName;
    private String phoneNumber;
    private String roadAddress;
    private String detailAddress;
    private String zipCode;

    public Orders toEntity(Member buyer) {
        return Orders.builder()
                .buyer(buyer)
                .deliverStatus(DeliverStatus.PREPARATION) // 주문 직후는 배송 준비 중
                .shippingAddress(new ShippingAddress(receiverName, phoneNumber, roadAddress, detailAddress, zipCode))
                .build();
    }
}