package com.aruba.Order.service.impl;

import com.aruba.Lib.component.ConfigProperties;
import com.aruba.Lib.entity.Cart;
import com.aruba.Lib.entity.CartItem;
import com.aruba.Lib.entity.Product;
import com.aruba.Lib.exception.ApiException;
import com.aruba.Lib.exception.ErrorCodes;
import com.aruba.Lib.exception.ResponseError;
import com.aruba.Lib.logging.logger.MsLogger;
import com.aruba.Lib.repository.CartItemRepository;
import com.aruba.Lib.service.ExternalCaller;
import com.aruba.Order.dto.AddToCartReqDto;
import com.aruba.Order.dto.ModifyCartReqDto;
import com.aruba.Order.service.CartItemService;
import com.aruba.Order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ExternalCaller externalCaller;

    @Autowired
    private ConfigProperties config;

    @Override
    public CartItem findById(Long id) {
        Optional<CartItem> res = cartItemRepository.findById(id);

        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("CartItem with id {%s} was not found.", id)).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }
        return res.get();
    }


    @Override
    public CartItem getByCartIdProductId(Long cartId, Long productId) {
        Optional<CartItem> res = cartItemRepository.findByCart_IdAndProduct_Id(cartId, productId);

        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("CartItem with cartId {%s} and productId {%s} was not found.", cartId, productId)).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }
        return res.get();
    }

    @Override
    public CartItem putToCart(AddToCartReqDto addToCartReqDto, Long userId) {
        String pUrl = "%s%s/products/%d".formatted(config.getApiGatewayHost(), config.getInventoryBasePath(), addToCartReqDto.getProduct_id());

        Product p = externalCaller.callGET(pUrl, Product.class);
        Cart userCart = cartService.findByUserOrCreate(userId);
        Optional<CartItem> ci = cartItemRepository.findByCart_IdAndProduct_Id(userCart.getId(), addToCartReqDto.getProduct_id());


        Integer productQuantity = addToCartReqDto.getQuantity();
        CartItem res;

        if (ci.isPresent()) {
            productQuantity += ci.get().getQuantity();
            checkStock(p, productQuantity);
            MsLogger.logger.debug("Cart Item exist, update");
            ci.get().setQuantity(productQuantity);
            res = cartItemRepository.save(ci.get());

        } else {
            checkStock(p, productQuantity);
            MsLogger.logger.debug("Cart Item not exist, insert");
            res = cartItemRepository.save(new CartItem(null, userCart, p, addToCartReqDto.getQuantity()));
        }

        userCart.setTotal(userCart.getTotal().add(p.getPrice().multiply(BigDecimal.valueOf(addToCartReqDto.getQuantity()))));
        cartService.updateCart(userCart);

        return res;
    }

    @Override
    public CartItem updateCart(ModifyCartReqDto modifyCartReqDto, Long userId) {

        String pUrl = "%s%s/products/%d".formatted(config.getApiGatewayHost(), config.getInventoryBasePath(), modifyCartReqDto.getProduct_id());
        Product p = externalCaller.callGET(pUrl, Product.class);
        Cart userCart = cartService.findByUser(userId);
        Optional<CartItem> ci = cartItemRepository.findByCart_IdAndProduct_Id(userCart.getId(), modifyCartReqDto.getProduct_id());

        if (ci.isPresent()) {
            int delta = modifyCartReqDto.getNewQuantity() - ci.get().getQuantity();
            if (delta != 0) {
                Integer quantity = ci.get().getQuantity() + delta;
                checkStock(p, quantity);
                MsLogger.logger.debug("New product quantity {}", quantity);
                ci.get().setQuantity(quantity);
                CartItem cin = cartItemRepository.save(ci.get());
                cartService.updateTotal(userId);
                return cin;
            } else {
                MsLogger.logger.debug("unchanged product quantity");
                return ci.get();
            }
        } else
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.BAD_REQUEST).message(String.format("Product %s does not exist in your Cart.", p.getName())).build());
    }

    @Override
    public Cart deleteFromCart(Long id, Long userId) {
        String pUrl = "%s%s/products/%d".formatted(config.getApiGatewayHost(), config.getInventoryBasePath(), id);
        Product p = externalCaller.callGET(pUrl, Product.class);

        Cart userCart = cartService.findByUser(userId);
        Optional<CartItem> ci = cartItemRepository.findByCart_IdAndProduct_Id(userCart.getId(), id);

        if (ci.isPresent()) {
            MsLogger.logger.debug("Cart Item exist, delete");

            userCart.setTotal(userCart.getTotal().subtract(p.getPrice().multiply(BigDecimal.valueOf(ci.get().getQuantity()))));
            cartItemRepository.delete(ci.get());
            return cartService.updateCart(userCart);
        } else
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.BAD_REQUEST).message(String.format("Product %s does not exist in your Cart.", p.getName())).build());

    }

    private void checkStock(Product p, Integer quantity) throws ApiException {
        if (p.getWarehouse().getStock() == 0)
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.BAD_REQUEST).message(String.format("Product %s out of stock.", p.getName())).build());
        else if (p.getWarehouse().getStock() < quantity) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.BAD_REQUEST).message(String.format("Out of Range. Max quantity for product %s is %s.", p.getName(), p.getWarehouse().getStock())).build());
        }
    }

    @Override
    public void deleteAll(List<CartItem> cartItemList) {
        cartItemRepository.deleteAll(cartItemList);
    }

}
