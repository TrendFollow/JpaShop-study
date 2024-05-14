package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // order만 persist하면 orderItem도 persist 된다.
    private List<OrderItem> orderItems = new ArrayList<>();
    
    // cascade : 다른 곳에서 참조하는 곳이 없고 같은 사이클을 돈다면 사용 가능
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // order만 persist하면 Delivery도 persist 된다.
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문시간

    private OrderStatus status; // 주문상태 [ORDER, CANCEL]

    //== 연관관계 메서드 ==//
    // 핵심적으로 컨트롤 하는 쪽에 양방향 메서드 생성
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this); // orders 를 넣는다.
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //== 생성 메서드 ==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //== 비즈니스 로직 ==//
    /**
     * 주문 취소
     */
    public void cancel(){
        // 배송 완료되었다면 취소 불가
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        // 한번 주문할 때 2개 이상 주문할 수 있으니 각각 cancel 날려줌
        for (OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    //== 조회 로직 ==//
    /**
     * 전체 주문 가격 조회
      */
    public int getTotalPrice(){
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
}
