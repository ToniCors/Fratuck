package com.aruba.Order.repository;

import com.aruba.Lib.entity.*;
import com.aruba.Lib.enums.OrderStatus;
import com.aruba.Lib.repository.CartItemRepository;
import com.aruba.Lib.repository.CartRepository;
import com.aruba.Lib.repository.OrderItemRepository;
import com.aruba.Lib.repository.OrderRepository;
import com.aruba.LibTest.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest
@Transactional
public class RepositoryTest {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    TestUtils testUtils = new TestUtils();


    @Test
    void findById() {

        Assertions.assertDoesNotThrow(() -> {
            System.out.println("CART");

            Optional<Cart> c = cartRepository.findById(1L);
            if (c.isPresent()) testUtils.writeValueAsString(c.get());
        });

        Assertions.assertDoesNotThrow(() -> {
            System.out.println("ORDER");
            Optional<Order> p = orderRepository.findById(15L);
            if (p.isPresent()) testUtils.writeValueAsString(p.get());
        });

        Assertions.assertDoesNotThrow(() -> {
            System.out.println("CART ITEMS");
            Optional<CartItem> ci = cartItemRepository.findById(1L);
            if (ci.isPresent()) {
                System.out.println("CART ID:" + ci.get().getCart().getId());
                System.out.println("PRODUCT ID:" + ci.get().getProduct().getId());
                testUtils.writeValueAsString(ci.get());
            }
        });

        Assertions.assertDoesNotThrow(() -> {
            System.out.println("ORDER ITEMS");
            Optional<OrderItem> oi = orderItemRepository.findById(1L);
            if (oi.isPresent()) {
                System.out.println("PRODUCT ID:" + oi.get().getProduct().getId());
                System.out.println("OREDR ID:" + oi.get().getOrder().getId());
                testUtils.writeValueAsString(oi.get());
            }
        });

    }

    @Test
    void save(){

        Order o = orderRepository.save(new Order(null, new User(1L), null, new BigDecimal(10), null, OrderStatus.NEW, "note"));
        Optional<Order> o2 = orderRepository.findById(o.getId());

        Assertions.assertDoesNotThrow(() -> {
            if (o2.isPresent()) testUtils.writeValueAsString(o2.get());
        });


    }

}
