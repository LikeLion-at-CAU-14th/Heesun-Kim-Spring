package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Orders;
import com.example.likelion14th_springboot.domain.Product;
import com.example.likelion14th_springboot.domain.ShippingAddress;
import com.example.likelion14th_springboot.domain.mapping.ProductOrders;
import com.example.likelion14th_springboot.dto.request.OrderCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.OrderDeleteRequestDto;
import com.example.likelion14th_springboot.dto.request.OrderItemRequestDto;
import com.example.likelion14th_springboot.dto.request.OrderUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.OrderResponseDto;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import com.example.likelion14th_springboot.enums.Role;
import com.example.likelion14th_springboot.repository.MemberRepository;
import com.example.likelion14th_springboot.repository.OrdersRepository;
import com.example.likelion14th_springboot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    // 주문 생성
    @Transactional
    public OrderResponseDto createOrder(OrderCreateRequestDto dto) {
        // 1. 구매자 조회
        Member buyer = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 구매자 권한 확인
        if (!Role.BUYER.equals(buyer.getRole())) {
            throw new IllegalArgumentException("주문은 구매자만 할 수 있습니다.");
        }

        // 3. 주문 생성
        Orders order = dto.toEntity(buyer);

        // 4. 상품마다 재고 확인하여 재고 차감
        int totalPrice = 0;
        for (OrderItemRequestDto item : dto.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. (id: " + item.getProductId() + ")"));

            if (product.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("재고가 부족합니다. (상품: " + product.getName() + ", 남은 재고: " + product.getStock() + ")");
            }

            product.reduceStock(item.getQuantity()); // 변경 감지로 자동 UPDATE
            totalPrice += product.getPrice() * item.getQuantity();

            order.addProductOrder(ProductOrders.builder()
                    .orders(order)
                    .product(product)
                    .quantity(item.getQuantity())
                    .build());
        }

        // 5. 잔액 확인 후 잔액 차감
        if (buyer.getDeposit() == null || buyer.getDeposit() < totalPrice) {
            throw new IllegalArgumentException("잔액이 부족합니다. (현재 잔액: " + buyer.getDeposit() + ", 결제 금액: " + totalPrice + ")");
        }
        buyer.useDeposit(totalPrice);

        // 6. 주문 저장
        Orders saved = ordersRepository.save(order);
        return OrderResponseDto.fromEntity(saved);
    }

    // 구매자별 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByBuyer(Long memberId) {
        // 1. 회원 존재 확인
        Member buyer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 해당 구매자의 주문 목록 조회
        return ordersRepository.findAllByBuyerIdAndDeletedFalse(buyer.getId()).stream()
                .map(OrderResponseDto::fromEntity)
                .toList();
    }

    // 주문 단건 조회
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        Orders order = ordersRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));
        return OrderResponseDto.fromEntity(order);
    }

    // 배송정보 수정
    @Transactional
    public OrderResponseDto updateShippingAddress(Long orderId, OrderUpdateRequestDto dto) {
        // 1. 회원 조회
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 주문 조회
        Orders order = ordersRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

        // 3. 본인 주문인지 권한 검증
        if (!order.getBuyer().getId().equals(member.getId())) {
            throw new IllegalArgumentException("본인의 주문만 수정할 수 있습니다.");
        }

        // 4. 방어 로직 - 배송 준비 중일 때만 수정 가능
        if (order.getDeliverStatus() != DeliverStatus.PREPARATION) {
            throw new IllegalArgumentException("배송 준비 중인 주문만 배송정보를 수정할 수 있습니다. (현재 상태: " + order.getDeliverStatus() + ")");
        }

        // 5. 배송정보 수정
        order.updateShippingAddress(new ShippingAddress(
                dto.getReceiverName(), dto.getPhoneNumber(), dto.getRoadAddress(),
                dto.getDetailAddress(), dto.getZipCode()));

        return OrderResponseDto.fromEntity(order);
    }

    // 주문 삭제
    @Transactional
    public void deleteOrder(Long orderId, OrderDeleteRequestDto dto) {
        // 1. 회원 조회
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 주문 조회
        Orders order = ordersRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

        // 3. 본인 주문인지 권한 검증
        if (!order.getBuyer().getId().equals(member.getId())) {
            throw new IllegalArgumentException("본인의 주문만 삭제할 수 있습니다.");
        }

        // 4. 방어 로직 - 배송 완료된 주문만 삭제 가능
        if (order.getDeliverStatus() != DeliverStatus.COMPLETED) {
            throw new IllegalArgumentException("배송 완료된 주문만 삭제할 수 있습니다. (현재 상태: " + order.getDeliverStatus() + ")");
        }

        // 5. Soft Delete
        order.softDelete();
    }
}