package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.enums.Role;
import com.example.likelion14th_springboot.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = AutowireMode.ALL) // 생성자 주입용
public class MemberServiceTest {

    private final MemberService memberService;

    private final MemberRepository memberRepository;

    MemberServiceTest(MemberService memberService, MemberRepository memberRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    @BeforeEach
    void setUp() {
        // DB 모든 Member 데이터 삭제
        // - 테스트 환경 (H2 등 인메모리 DB) : 사용 O
        // - 실제 운영 (Production) DB : 사용 X
        memberRepository.deleteAll();

        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member = Member.builder()
                    .name("user" + i)
                    .email("user" + i + "@test.com")
                    .address("서울시 테스트동 " + i + "번지")
                    .phoneNumber("010-1234-56" + String.format("%02d", i))
                    .deposit(1000 * i)
                    .age(15 + i) // 16 ~ 45세, i>=5(user5~user30)일 때 20세 이상
                    .isAdmin(false)
                    .role(Role.BUYER)
                    .build();

            memberRepository.save(member);
        });
    }

    @Test
    @DisplayName("모든 회원을 조회한다.")
    void testGetAllMembers() {
        List<Member> memberList = memberService.getAllMembers();
        assertEquals(30, memberList.size());

        Member member = memberList.stream()
                .filter(m -> m.getName().equals("user15"))
                .findFirst()
                .orElseThrow();
    }

    @Test
    @DisplayName("이메일로 회원을 조회한다.")
    void testGetByEmail() {
        Member actual = memberService.getByEmail("user1@test.com");

        assertEquals(actual.getName(), "user1");
    }

    @Test
    @DisplayName("이메일로 없는 회원을 조회할 경우 예외를 던진다.")
    void ThrowsExceptionIfEmailDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.getByEmail("none@test.com");
        });
    }

    @Test
    @DisplayName("회원 목록을 ID 기준으로 내림차순 조회 시 페이지 정보가 올바르게 반환된다.")
    void testGetMembersByPage() {
        Page<Member> page = memberService.getMembersByPage(0, 10);

        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(30);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getContent().get(0).getName()).isEqualTo("user30");
    }

    @Test
    @DisplayName("이름이 주어진 값으로 시작하는 경우만 필터링")
    void testGetMembersByNamePrefix() {
        // "user1"로 시작하는 회원: user1, user10 ~ user19 (총 11명)
        List<Member> expected = memberRepository.findAll().stream()
                .filter(m -> m.getName().startsWith("user1"))
                .toList();

        List<Member> actual = memberService.getMembersByNamePrefix("user1");

        assertThat(actual).hasSize(expected.size());
        assertThat(actual).hasSize(11);
        assertThat(actual).allMatch(m -> m.getName().startsWith("user1"));
    }

    @Test
    @DisplayName("나이가 20 이상이고 이름 기준 오름차순 정렬된 페이징 결과 반환")
    void testGetAdultMembersSortedByName() {
        // age = 15 + i (i: 1~30) 이므로 20세 이상인 회원은 i=5~30, 총 26명
        List<Member> expectedSorted = memberRepository.findAll().stream()
                .filter(m -> m.getAge() >= 20)
                .sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
                .toList();

        Page<Member> page = memberService.getAdultMembersSortedByName(0, 10);

        assertThat(page.getTotalElements()).isEqualTo(26);
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getContent()).allMatch(m -> m.getAge() >= 20);

        List<String> actualNames = page.getContent().stream().map(Member::getName).toList();
        List<String> expectedNames = expectedSorted.stream().map(Member::getName).limit(10).toList();
        assertThat(actualNames).isEqualTo(expectedNames);

        // 오름차순 정렬 검증
        for (int i = 0; i < actualNames.size() - 1; i++) {
            assertThat(actualNames.get(i).compareTo(actualNames.get(i + 1))).isLessThanOrEqualTo(0);
        }
    }
}
