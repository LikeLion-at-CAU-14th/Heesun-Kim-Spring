package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    // 이름이 주어진 값으로 시작하는 회원 필터링
    List<Member> findByNameStartingWith(String name);

    // 나이가 특정 값 이상인 회원을 페이징 조회 (정렬은 Pageable에 위임)
    Page<Member> findByAgeGreaterThanEqual(Integer age, Pageable pageable);
}