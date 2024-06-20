package com.aruba.Order.controller;

import com.aruba.Lib.entity.Cart;
import com.aruba.Lib.logging.logger.MsLogger;
import com.aruba.Order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.aruba.Lib.utils.Constant.APP_TOKEN;

@RestController
@RequestMapping(path = "/carts", produces = "application/json")
public class CartController {

    @Autowired
    private CartService cartService;


    @GetMapping(path = "/{id}")
    public ResponseEntity<Cart> getById(@PathVariable Long id) {
        MsLogger.logger.info("Get Cart by ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(cartService.findById(id));
    }

    @GetMapping(path = "/user/{id}")
    public ResponseEntity<Cart> getByUser(@PathVariable Long id) {
        MsLogger.logger.info("Get Cart by User: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(cartService.findByUser(id));
    }

    @PutMapping(path = "/create")
    public ResponseEntity<Cart> create(@RequestHeader(APP_TOKEN) Long auth) {
        MsLogger.logger.info("Create Cart for user with id {}", auth);
        return ResponseEntity.status(HttpStatus.OK).body(cartService.findByUserOrCreate(auth));

    }

    @PostMapping(path = "/update/total/{id}")
    public ResponseEntity<Cart> updateTotal(@PathVariable Long id) {
        MsLogger.logger.info("updateTotal cart id {}", id);
        cartService.updateTotal(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();

    }



}