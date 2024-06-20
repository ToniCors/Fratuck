package com.aruba.Order.controller;

import com.aruba.Lib.entity.CartItem;
import com.aruba.Lib.logging.logger.MsLogger;
import com.aruba.Order.dto.AddToCartReqDto;
import com.aruba.Order.dto.ModifyCartReqDto;
import com.aruba.Order.service.CartItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.aruba.Lib.utils.Constant.APP_TOKEN;

@RestController
@RequestMapping(path = "/cartItems", produces = "application/json")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;


    @PutMapping(path = "/put")
    @Validated
    public ResponseEntity<?> putToCart(@Valid @RequestBody AddToCartReqDto req,@RequestHeader(APP_TOKEN) Long auth) {
        MsLogger.logger.info("putToCart: {}", req.toString());
        cartItemService.putToCart(req,auth);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();

    }

    @PostMapping(path = "/update")
    @Validated
    public ResponseEntity<?> updateCart(@Valid @RequestBody ModifyCartReqDto req,@RequestHeader(APP_TOKEN) Long auth) {
        MsLogger.logger.info("updateCart: {}", req.toString());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(cartItemService.updateCart(req,auth));

    }

    @DeleteMapping(path = "/delete/{id}")
    @Validated
    public ResponseEntity<?> deleteFromCart(@Valid @PathVariable Long id,@RequestHeader(APP_TOKEN) Long auth) {
        MsLogger.logger.info("deleteFromCart: {}", id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(cartItemService.deleteFromCart(id, auth));

    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CartItem> getById(@PathVariable Long id) {
        MsLogger.logger.info("Get CartItem by ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(cartItemService.findById(id));

    }

    @GetMapping(path = "/cart/{cartId}/product/{productId}")
    public ResponseEntity<CartItem> getCartItemByCartIdProductId(@PathVariable Long cartId,@PathVariable Long productId) {
        MsLogger.logger.info("Get cartItems By CartId: {} and ProductId: {}", cartId,productId);
        return ResponseEntity.status(HttpStatus.OK).body(cartItemService.getByCartIdProductId(cartId,productId));

    }
}
