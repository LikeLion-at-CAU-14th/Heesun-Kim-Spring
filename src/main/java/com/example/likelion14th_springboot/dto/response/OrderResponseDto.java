package com.example.likelion14th_springboot.dto.response;

import com.example.likelion14th_springboot.domain.Orders;
import com.example.likelion14th_springboot.domain.ShippingAddress;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private Long buyerId;
    private DeliverStatus deliverStatus;
    private List<OrderItemResponseDto> items;
    private Integer totalPrice;

    // 배송정보 5개
    private String receiverName;
    private String phoneNumber;
    private String roadAddress;
    private String detailAddress;
    private String zipCode;

    public static OrderResponseDto fromEntity(Orders order) {
        List<OrderItemResponseDto> items = order.getProductOrders().stream()
                .map(OrderItemResponseDto::fromEntity)
                .toList();

        int totalPrice = 0;
        for (OrderItemResponseDto item : items) {
            totalPrice += item.getPrice() * item.getQuantity();
        }

        ShippingAddress address = order.getShippingAddress();

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .buyerId(order.getBuyer().getId())
                .deliverStatus(order.getDeliverStatus())
                .items(items)
                .totalPrice(totalPrice)
                .receiverName(address.getReceiverName())
                .phoneNumber(address.getPhoneNumber())
                .roadAddress(address.getRoadAddress())
                .detailAddress(address.getDetailAddress())
                .zipCode(address.getZipCode())
                .build();
    }
}