package com.aruba.Order.controller;

import com.aruba.Lib.entity.Order;
import com.aruba.Lib.enums.OrderStatus;
import com.aruba.Lib.logging.logger.MsLogger;
import com.aruba.Order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.aruba.Lib.utils.Constant.APP_TOKEN;


@RestController
@RequestMapping(path = "/orders", produces = "application/json")
public class OrderController {

    @Autowired
    private OrderService orderService;



    @GetMapping(path = "/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        MsLogger.logger.info("Get Order by ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findById(id));

    }

    @GetMapping
    @Operation(parameters = {@Parameter(in = ParameterIn.QUERY, description = "Page you want to retrieve (0..N)", name = "page", content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))), @Parameter(in = ParameterIn.QUERY, description = "Number of records per page.", name = "size", content = @Content(schema = @Schema(type = "integer", defaultValue = "20"))), @Parameter(in = ParameterIn.QUERY, description = "Sorting criteria in the format: property(,asc|desc). " + "Default sort order is ascending. " + "Multiple sort criteria are supported.", name = "sort", content = @Content(array = @ArraySchema(schema = @Schema(type = "string"))))})
    public ResponseEntity<Page<Order>> findAll(@Parameter(hidden = true) Pageable pageable) {
        MsLogger.logger.info("Get All Order");

        return ResponseEntity.status(HttpStatus.OK).body(orderService.findAll(pageable));
    }

    @GetMapping(path = "/user/{id}")
    @Operation(parameters = {@Parameter(in = ParameterIn.QUERY, description = "Page you want to retrieve (0..N)", name = "page", content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))), @Parameter(in = ParameterIn.QUERY, description = "Number of records per page.", name = "size", content = @Content(schema = @Schema(type = "integer", defaultValue = "20"))), @Parameter(in = ParameterIn.QUERY, description = "Sorting criteria in the format: property(,asc|desc). " + "Default sort order is ascending. " + "Multiple sort criteria are supported.", name = "sort", content = @Content(array = @ArraySchema(schema = @Schema(type = "string"))))})
    public ResponseEntity<Page<Order>> findAllByUser(@PathVariable Long id, @Parameter(hidden = true) Pageable pageable) {
        MsLogger.logger.info("Get All Order by User {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findAllByUser(id, pageable));
    }

    @PutMapping(path = "/put")
    @Validated
    public ResponseEntity<Order> createOrder(@RequestHeader(APP_TOKEN) Long auth) {
        MsLogger.logger.info("createOrder. Auth Token {}",auth);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.createOrder(auth));

    }

    @PutMapping(path = "/put/{cartId}")
    @Validated
    public ResponseEntity<Order> createOrder(@RequestHeader(APP_TOKEN) Long auth, @PathVariable Long cartId) {
        MsLogger.logger.info("createOrder. Auth Token {}",auth);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.createOrder(auth, cartId));

    }

    @PutMapping(path = "/save")
    @Validated
    public ResponseEntity<Order> save() {
        MsLogger.logger.info("createOrder:");
        return ResponseEntity.status(HttpStatus.OK).body(orderService.save());

    }

    @PostMapping(path = "/{id}/updateStatus/{status}")
    @Validated
    public ResponseEntity<Order> updateOrderStatus(@PathVariable("id") Long id, @PathVariable("status") OrderStatus status) {
        MsLogger.logger.info("updateOrderStatus:");
        return ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrderStatus(id,status));

    }

//    @Autowired
//    private MessageSender messageSender;
//
//    @GetMapping(path = "/rabbit/{id}")
//    public void debugRabbit(@PathVariable Long id) {
//        MsLogger.logger.info("message");
//        Order o = this.getById(id).getBody();
//        messageSender.sendMessage(o);
//    }

}
