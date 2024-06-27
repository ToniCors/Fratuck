package com.aruba.Order.service.impl;

import com.aruba.Lib.entity.Cart;
import com.aruba.Lib.entity.User;
import com.aruba.Lib.exception.ApiException;
import com.aruba.Lib.exception.ErrorCodes;
import com.aruba.Lib.exception.ResponseError;
import com.aruba.Lib.repository.CartRepository;
import com.aruba.Order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository repository;


    @Override
    public Cart findByCartIdUserId(Long cartId, Long userId) {
        Optional<Cart> res = repository.findByIdAndUser_Id(cartId, userId);

        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("Cart with id {%s} not found.", cartId)).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }

        return res.get();
    }

    @Override
    public Cart findById(Long id) {

        Optional<Cart> res = repository.findById(id);

        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("Cart with id {%s} was not found.", id)).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }

        return res.get();
    }

    @Override
    public Cart findByUser(Long userId) {

        Optional<Cart> res = repository.findByUser_Id(userId);
        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("Cart for User with id {%s} was not found.", userId)).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }
        return res.get();
    }

    @Override
    public Cart findByUserOrCreate(Long userId) {

        Optional<Cart> res = repository.findByUser_Id(userId);
        return res.orElseGet(() -> this.createCart(userId));

    }

    @Override
    public Cart createCart(Long userId) {
        return repository.save(new Cart(null, new User(userId), new BigDecimal(0)));

    }

    @Override
    public Cart updateCart(Cart c) {
        return repository.save(c);
    }

    @Override
    public void delete(Cart c) {
        repository.delete(c);
    }

    @Override
    public Integer updateTotal(Long id){
        return repository.updateTotal(id);
    }
}
