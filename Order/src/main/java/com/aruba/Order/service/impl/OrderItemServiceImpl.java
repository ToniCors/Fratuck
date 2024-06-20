package com.aruba.Order.service.impl;

import com.aruba.Lib.dto.OrderProductCount;
import com.aruba.Lib.entity.OrderItem;
import com.aruba.Lib.exception.ApiException;
import com.aruba.Lib.exception.ErrorCodes;
import com.aruba.Lib.exception.ResponseError;
import com.aruba.Lib.repository.OrderItemRepository;
import com.aruba.Order.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository repository;


    @Override
    public OrderItem findById(Long id) {
        Optional<OrderItem> res = repository.findById(id);

        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("Entity with id {%s} was not found.", id)).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }

        return res.get();
    }

    @Override
    public List<OrderProductCount> groupByProduct_id(Long orderId){

        return repository.groupByProduct_id(orderId);
    }
}
