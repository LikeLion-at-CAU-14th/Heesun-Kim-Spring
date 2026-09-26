package com.example.likelion14th_springboot.dto.response;

import com.example.likelion14th_springboot.domain.mapping.ProductOrders;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderItemResponseDto {
    private Long productId;
    private String productName;
    private Integer price;
    private Integer quantity;

    public static OrderItemResponseDto fromEntity(ProductOrders productOrders) {
        return OrderItemResponseDto.builder()
                .productId(productOrders.getProduct().getId())
                .productName(productOrders.getProduct().getName())
                .price(productOrders.getProduct().getPrice())
                .quantity(productOrders.getQuantity())
                .build();
    }
}