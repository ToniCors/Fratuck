package com.aruba.Order.service.impl;

import com.aruba.Lib.entity.*;
import com.aruba.Lib.enums.OrderStatus;
import com.aruba.Lib.enums.PaymentStatus;
import com.aruba.Lib.exception.ApiException;
import com.aruba.Lib.exception.ErrorCodes;
import com.aruba.Lib.exception.ResponseError;
import com.aruba.Lib.logging.logger.MsLogger;
import com.aruba.Lib.repository.OrderRepository;
import com.aruba.Lib.service.MessageSender;
import com.aruba.Order.service.CartItemService;
import com.aruba.Order.service.CartService;
import com.aruba.Order.service.OrderItemService;
import com.aruba.Order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private MessageSender messageSender;

    @Override
    public Order findById(Long id) {
        Optional<Order> res = orderRepository.findById(id);

        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("Order with id {%s} was not found.", id)).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }

        return res.get();
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> findAllByUser(Long id, Pageable pageable) {
        return orderRepository.findByUser_Id(id, pageable);
    }

    @Override
    public Order save() {
        return null;
    }

    @Override
    public Order createOrder(Long userId) {
        Cart c = cartService.findByUser(userId);

        return createOrder(userId, c);
    }

    @Override
    public Order createOrder(Long userId, Long cartId) {
        Cart c = cartService.findByUser(cartId);

        return createOrder(userId, c);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order o = this.findById(orderId);
        o.setStatus(newStatus);
        return orderRepository.save(o);
    }

    private Order createOrder(Long userId, Cart c) {
        if (c.getCartItems().isEmpty())
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.BAD_REQUEST).message("Impossible create order, Cart is empty").build());
        else {
            Order newOrder = new Order();
            newOrder.setId(null);
            newOrder.setTotal(c.getTotal());
            newOrder.setStatus(OrderStatus.NEW);
            newOrder.setPayment(new Payment(null, newOrder, PaymentStatus.AWAITING));
            newOrder.setUser(new User(userId));

            List<OrderItem> oi = new ArrayList<>();
            newOrder.setOrderItems(oi);

            for (CartItem ci : c.getCartItems()) {

                if (ci.getQuantity() <= ci.getProduct().getWarehouse().getStock())
                    for (int i = 0; i < ci.getQuantity(); i++) {
                        oi.add(new OrderItem(newOrder, ci.getProduct()));
                    }
                else
                    throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.BAD_REQUEST).message(String.format("Product %s no more available. Please update your Cart", ci.getProduct().getName())).build());

            }

            Order o = orderRepository.save(newOrder);
            MsLogger.logger.info("Order Create with ID: {}", o.getId());

            MsLogger.logger.debug("Empty Carrello");
            cartItemService.deleteAll(c.getCartItems());
            c.setTotal(new BigDecimal(0));

            MsLogger.logger.debug("Send Message");
            messageSender.sendMessage(o);
            return o;
        }
    }
}
