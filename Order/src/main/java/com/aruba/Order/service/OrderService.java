package com.aruba.Order.service;

import com.aruba.Lib.entity.Order;
import com.aruba.Lib.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface OrderService {

    Order findById(Long id);

    Page<Order> findAll(Pageable pageable);

    Page<Order> findAllByUser(Long id, Pageable pageable);

    Order save();

    Order createOrder(Long userId);

    Order createOrder(Long userId,Long cartId);
    Order updateOrderStatus(Long orderId, OrderStatus newStatus);
}
