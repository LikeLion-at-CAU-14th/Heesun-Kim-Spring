package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.dto.request.JoinRequestDto;
import com.example.likelion14th_springboot.dto.response.JoinResponseDto;
import com.example.likelion14th_springboot.exception.CustomException;
import com.example.likelion14th_springboot.exception.ErrorCode;
import com.example.likelion14th_springboot.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다: " + email));

    }

    public Page<Member> getMembersByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return memberRepository.findAll(pageable);
    }

    // 이름이 주어진 값으로 시작하는 회원만 필터링
    public List<Member> getMembersByNamePrefix(String name) {
        return memberRepository.findByNameStartingWith(name);
    }

    // 나이가 20 이상인 회원만, 이름 기준 오름차순 정렬된 페이징 결과 반환
    public Page<Member> getAdultMembersSortedByName(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return memberRepository.findByAgeGreaterThanEqual(20, pageable);
    }

    // 비밀번호 인코더 DI(생성자 주입)
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public JoinResponseDto join(JoinRequestDto joinRequestDto) {
        // 해당 name이 이미 존재하는 경우 -> 409 Conflict
        if (memberRepository.existsByName(joinRequestDto.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_MEMBER_NAME);
        }

        // 유저 객체 생성
        Member member = joinRequestDto.toEntity(bCryptPasswordEncoder);

        // 유저 정보 저장
        Member saved = memberRepository.save(member);
        return JoinResponseDto.fromEntity(saved);
    }
}


