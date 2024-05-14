package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;


    @Test
    @DisplayName("회원가입 테스트")
    public void 회원가입() throws Exception{
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long savedId = memberService.join(member);

        // then
//        em.flush(); // DB에 쿼리가 강제로 나간다.
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    @DisplayName("중복회원 테스트")
    public void 중복_회원_예외() throws Exception{
        // given
        Member member1 = new Member();
        member1.setName("kim1");

        Member member2 = new Member();
        member2.setName("kim1");

        // when
        memberService.join(member1);
        try{
            memberService.join(member2); // 예외가 발생해야 한다.!!!
        }catch (IllegalStateException e){
            return;
        }

        // then
        fail("예외가 발생해야 합니다.");
    }


}