package com.aruba.Order.service;

import com.aruba.Lib.entity.Cart;

public interface CartService {

    Cart findByCartIdUserId(Long cartId,Long userId);
    Cart findById(Long id);
    Cart findByUser(Long userId);

    Cart findByUserOrCreate(Long userId);

    Cart createCart(Long userId);
    Cart updateCart(Cart c);
    void delete(Cart c);

    Integer updateTotal(Long id);
}
