package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception{
        // given
        Book book = em.find(Book.class, 1l);

        // 트랜젝션 내에서 이름이 바뀌고 트랜잭션에 커밋이 되버리면 JPA가 변경분에 대해 업데이트 쿼리 자동 생성
        book.setName("abcde");
        // 변경 감지 == dirty checking

        // when

        // then
    }
}
