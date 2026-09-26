package com.example.likelion14th_springboot.controller;

import com.example.likelion14th_springboot.dto.request.OrderCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.OrderDeleteRequestDto;
import com.example.likelion14th_springboot.dto.request.OrderUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.OrderResponseDto;
import com.example.likelion14th_springboot.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderCreateRequestDto dto) {
        return ResponseEntity.ok(orderService.createOrder(dto));
    }

    // 구매자별 주문 목록 조회
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrdersByBuyer(@RequestParam Long memberId) {
        return ResponseEntity.ok(orderService.getOrdersByBuyer(memberId));
    }

    // 주문 단건 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // 배송정보 수정
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> updateShippingAddress(@PathVariable Long orderId,
                                                                  @RequestBody OrderUpdateRequestDto dto) {
        return ResponseEntity.ok(orderService.updateShippingAddress(orderId, dto));
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId,
                                              @RequestBody OrderDeleteRequestDto dto) {
        orderService.deleteOrder(orderId, dto);
        return ResponseEntity.ok("주문이 성공적으로 삭제되었습니다.");
    }
}