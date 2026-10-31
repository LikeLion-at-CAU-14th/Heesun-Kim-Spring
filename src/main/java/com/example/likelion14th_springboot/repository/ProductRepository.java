package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// <매핑할 Entity 클래스, ID의 PK 데이터 타입>
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 판매자가 등록한 상품 전체 조회
    List<Product> findAllBySeller(Member seller);
}