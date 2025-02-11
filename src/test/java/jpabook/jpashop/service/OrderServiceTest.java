package jpabook.jpashop.service;

import jpabook.jpashop.Domain.Address;
import jpabook.jpashop.Domain.Item.Book;
import jpabook.jpashop.Domain.Item.Item;
import jpabook.jpashop.Domain.Member;
import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Domain.OrderStatus;
import jpabook.jpashop.Exception.NotEnoughStockException;
import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.Service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        Member member = createMember();
        Book book = createBook("시공 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 수가 정확해야 한다", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격*수량 이다", 10000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량 만큼 재고가 줄어야 한다", 8, book.getStockQuantity());
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);

        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "경기", "123-123"));
        em.persist(member);

        return member;
    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고초과() throws Exception {
        Member member = createMember();
        Item item = createBook("시공 JPA", 10000, 10);

        int orderCount = 11;

        orderService.order(member.getId(), item.getId(), orderCount);

        fail("재고 수량 부족 예외가 발생해야 한다");
    }

    @Test
    public void 주문취소() throws Exception {
        Member member = createMember();
        Item item = createBook("시공 JPA", 10000, 10);
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        orderService.cancelOrder(orderId);

        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCEL 이다.", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문 취소된 상품은 재고가 증가해야 한다.", 10, item.getStockQuantity());
    }
}