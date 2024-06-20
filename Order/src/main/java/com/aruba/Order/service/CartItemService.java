package com.aruba.Order.service;

import com.aruba.Lib.entity.Cart;
import com.aruba.Lib.entity.CartItem;
import com.aruba.Order.dto.AddToCartReqDto;
import com.aruba.Order.dto.ModifyCartReqDto;

import java.util.List;

public interface CartItemService {

    CartItem findById(Long id);

    CartItem getByCartIdProductId(Long cartId, Long productId);

    CartItem putToCart(AddToCartReqDto addToCartReqDto, Long userId);

    CartItem updateCart(ModifyCartReqDto modifyCartReqDto, Long userId);

    Cart deleteFromCart(Long removeFromCartReqDto, Long userId);

    void deleteAll(List<CartItem> cartItemList);
}
