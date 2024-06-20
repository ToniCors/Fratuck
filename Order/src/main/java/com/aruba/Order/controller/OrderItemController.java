package com.aruba.Order.controller;

import com.aruba.Lib.dto.OrderProductCount;
import com.aruba.Lib.logging.logger.MsLogger;
import com.aruba.Order.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/orderItems", produces = "application/json")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;
    @GetMapping(path = "/group/product/{id}")
    public ResponseEntity<List<OrderProductCount>> groupByProduct_id(@PathVariable Long id, @RequestHeader HashMap<String,String> headers) {

        System.out.println("");
        MsLogger.logger.info("GroupByProduct_id ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(orderItemService.groupByProduct_id(id));
    }}
