package com.aruba.Order.service.impl;

import com.aruba.Lib.component.ConfigProperties;
import com.aruba.Lib.entity.*;
import com.aruba.Lib.enums.PaymentStatus;
import com.aruba.Lib.exception.ApiException;
import com.aruba.Lib.repository.CartItemRepository;
import com.aruba.Lib.repository.OrderRepository;
import com.aruba.Lib.service.ExternalCaller;
import com.aruba.Order.service.CartItemService;
import com.aruba.Order.service.CartService;
import com.aruba.Order.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Transactional
class OrderServiceImplTest {

    @MockBean
    private ExternalCaller externalCaller;
    @MockBean
    private CartService cartService;
    @MockBean
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ConfigProperties config;


    @Test
    void findById() {
    }

    @Test
    void findAllByUser() {
    }

    @Test
    void whenCreateOrder_thenOrderIsReturned() {

        List<CartItem> ci = new ArrayList<>();

        Cart c = new Cart();
        c.setTotal(new BigDecimal(10));
        c.setId(1L);
        c.setCartItems(ci);

        Product p = new Product(1L, "name", "desc", new BigDecimal(10));
        p.setWarehouse(new Warehouse(p.getId(), 10L));
        ci.add(new CartItem(1L, c, p, 1));

        Order o = new Order();
        o.setId(100L);
        o.setPayment(new Payment(1L, o, PaymentStatus.AWAITING));

        Mockito.when(cartService.findByUser(any())).thenReturn(c);
        Mockito.when(orderRepository.save(any())).thenReturn(o);


        orderService.createOrder(1L);

        Assertions.assertNotNull(o);
        Assertions.assertEquals(PaymentStatus.AWAITING, o.getPayment().getStatus());


    }

    @Test
    void whenCreateOrder_andCartIsEmpty_thenApiException() {
        List<CartItem> ci = new ArrayList<>();

        Cart c = new Cart();
        c.setTotal(new BigDecimal(10));
        c.setId(1L);
        c.setCartItems(ci);

        Product p = new Product(1L, "name", "desc", new BigDecimal(10));
        p.setWarehouse(new Warehouse(p.getId(), 10L));
//        ci.add(new CartItem(1L,c,p,1));

        Order o = new Order();
        o.setId(100L);
        o.setPayment(new Payment(1L, o, PaymentStatus.AWAITING));

        Mockito.when(cartService.findByUser(any())).thenReturn(c);
        Mockito.when(orderRepository.save(any())).thenReturn(o);

        ApiException e = Assertions.assertThrows(ApiException.class, () -> {
            orderService.createOrder(1L);
        });


        Assertions.assertEquals("Impossible create order, Cart is empty", e.getMessage());
    }

    @Test
    void whenCreateOrder_andCartNotExist_thenApiException() {

        List<CartItem> ci = new ArrayList<>();

        Cart c = new Cart();
        c.setTotal(new BigDecimal(10));
        c.setId(1L);
        c.setCartItems(ci);

        Product p = new Product(1L, "name", "desc", new BigDecimal(10));
        p.setWarehouse(new Warehouse(p.getId(), 10L));
        ci.add(new CartItem(1L,c,p,100));

        Order o = new Order();
        o.setId(100L);
        o.setPayment(new Payment(1L, o, PaymentStatus.AWAITING));

        Mockito.when(cartService.findByUser(any())).thenReturn(c);
        Mockito.when(orderRepository.save(any())).thenReturn(o);

        ApiException e = Assertions.assertThrows(ApiException.class, () -> {
            orderService.createOrder(1L);
        });


        Assertions.assertTrue(e.getMessage().endsWith("Please update your Cart"));
    }

}