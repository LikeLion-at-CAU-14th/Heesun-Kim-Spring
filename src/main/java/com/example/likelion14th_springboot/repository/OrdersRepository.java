package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    // 구매자별 주문 목록
    List<Orders> findAllByBuyerIdAndDeletedFalse(Long buyerId);

    // 주문 단건 조회
    Optional<Orders> findByIdAndDeletedFalse(Long id);
}