package jpabook.jpashop.Service;

import jpabook.jpashop.Domain.Delivery;
import jpabook.jpashop.Domain.Item.Item;
import jpabook.jpashop.Domain.Member;
import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Domain.OrderItem;
import jpabook.jpashop.Repository.ItemRepository;
import jpabook.jpashop.Repository.MemberRepositoryOld;
import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.Repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor // repository setter 없애짐
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepositoryOld memberRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장, CascadeType.ALL 로 인해 관계 엔티티 같이 persist
        orderRepository.save(order);

        return order.getId();
    }

    //취소
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }

    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAll(orderSearch);
    }

    //검색
//    public List<Order> findOrders(OrderSearch orderSearch) {
//        return orderRepository.findAll(orderSearch);
//    }
}
