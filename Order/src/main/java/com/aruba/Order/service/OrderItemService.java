package com.aruba.Order.service;

import com.aruba.Lib.dto.OrderProductCount;
import com.aruba.Lib.entity.OrderItem;

import java.util.List;

public interface OrderItemService {

    OrderItem findById(Long id);

    List<OrderProductCount> groupByProduct_id(Long orderId);
}
