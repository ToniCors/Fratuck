package com.aruba.Order.service.impl;

import com.aruba.Lib.component.ConfigProperties;
import com.aruba.Lib.entity.*;
import com.aruba.Lib.exception.ApiException;
import com.aruba.Lib.repository.CartItemRepository;
import com.aruba.Lib.service.ExternalCaller;
import com.aruba.Order.dto.AddToCartReqDto;
import com.aruba.Order.dto.DeleteFromCartReqDto;
import com.aruba.Order.dto.ModifyCartReqDto;
import com.aruba.Order.service.CartItemService;
import com.aruba.Order.service.CartService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@Transactional
class CartItemServiceImplTest {

    @MockBean
    private ExternalCaller externalCaller;
    @MockBean
    private CartService cartService;
    @MockBean
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private ConfigProperties config;

    @Test
    void whenAddToCartThenOutOfRangeIsReturned() {

        Product p = new Product();
        p.setId(1L);
        p.setName("test");
        p.setWarehouse(new Warehouse(1L, 5L));

        Mockito.when(externalCaller.callGET(any(), eq(Product.class))).thenReturn(p);
        Mockito.when(cartService.findByUser(any())).thenReturn(new Cart(1L));

        ApiException e = Assertions.assertThrows(ApiException.class, () -> {
            cartItemService.putToCart(new AddToCartReqDto(1L, 10), 1L);
        });

        Assertions.assertTrue(e.getResponseError().getMessage().startsWith("Out of Range"));
    }

    @Test
    void whenAddToCartThenOutOfStockIsReturned() {
        Product p = new Product();
        p.setId(1L);
        p.setName("test");
        p.setWarehouse(new Warehouse(1L, 0L));

        Mockito.when(externalCaller.callGET(any(), eq(Product.class))).thenReturn(p);
        Mockito.when(cartService.findByUser(any())).thenReturn(new Cart(1L));

        ApiException e = Assertions.assertThrows(ApiException.class, () -> {
            cartItemService.putToCart(new AddToCartReqDto(1L, 10),1L);
        });

        Assertions.assertTrue(e.getResponseError().getMessage().endsWith("out of stock."));
    }

    @Test
    void whenAddToCartAndCartItemNotExistThenCartItemIsReturned() {
        Product p = new Product();
        p.setId(1L);
        p.setName("test");
        p.setPrice(new BigDecimal(10));
        p.setWarehouse(new Warehouse(1L, 5L));

        Cart c = new Cart(1L);
        c.setTotal(new BigDecimal(0));

        Mockito.when(externalCaller.callGET(any(), eq(Product.class))).thenReturn(p);
        Mockito.when(cartService.findByUser(any())).thenReturn(c);
        Mockito.when(cartItemRepository.save(any())).thenReturn(new CartItem(1L));
        Mockito.when(cartItemRepository.findByCart_IdAndProduct_Id(eq(c.getId()), eq(p.getId()))).thenReturn(Optional.empty());


        Assertions.assertEquals(new BigDecimal(0), c.getTotal());
        AddToCartReqDto dto = new AddToCartReqDto(1L, 1);
        CartItem ci = cartItemService.putToCart(dto,1L);

        Assertions.assertEquals(1L, ci.getId());

        Assertions.assertEquals(p.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())), c.getTotal());
    }

    @Test
    void whenAddToCartAndCartItemExistThenCartItemIsReturned() {
        Product p = new Product();
        p.setId(1L);
        p.setName("test");
        p.setPrice(new BigDecimal(10));
        p.setWarehouse(new Warehouse(1L, 5L));

        Cart c = new Cart(1L);
        c.setTotal(new BigDecimal(30));

        CartItem ci = new CartItem(1L);
        ci.setProduct(p);
        ci.setCart(c);
        ci.setQuantity(3);

        Mockito.when(externalCaller.callGET(any(), eq(Product.class))).thenReturn(p);
        Mockito.when(cartService.findByUser(any())).thenReturn(c);
        Mockito.when(cartItemRepository.save(any())).thenReturn(new CartItem(1L));
        Mockito.when(cartItemRepository.findByCart_IdAndProduct_Id(eq(c.getId()), eq(p.getId()))).thenReturn(Optional.of(ci));

        Assertions.assertEquals(new BigDecimal(30), c.getTotal());
        AddToCartReqDto dto = new AddToCartReqDto(1L, 1);
        ci = cartItemService.putToCart(dto,1L);

        Assertions.assertEquals(1L, ci.getId());

        Assertions.assertEquals(new BigDecimal(40), c.getTotal());

    }

    @Test
    void whenAddToCartAndCartItemExistThenCartItemIsReturnedOutOfRange() {
        Product p = new Product();
        p.setId(1L);
        p.setName("test");
        p.setPrice(new BigDecimal(10));
        p.setWarehouse(new Warehouse(1L, 5L));

        Cart c = new Cart(1L);
        c.setTotal(new BigDecimal(30));

        CartItem ci = new CartItem(1L);
        ci.setProduct(p);
        ci.setCart(c);
        ci.setQuantity(3);

        Mockito.when(externalCaller.callGET(any(), eq(Product.class))).thenReturn(p);
        Mockito.when(cartService.findByUser(any())).thenReturn(c);
        Mockito.when(cartItemRepository.save(any())).thenReturn(new CartItem(1L));
        Mockito.when(cartItemRepository.findByCart_IdAndProduct_Id(eq(c.getId()), eq(p.getId()))).thenReturn(Optional.of(ci));

        Assertions.assertEquals(new BigDecimal(30), c.getTotal());
        AddToCartReqDto dto = new AddToCartReqDto(1L, 10);
        ApiException e = Assertions.assertThrows(ApiException.class, () -> {
            cartItemService.putToCart(dto,1L);
        });

        Assertions.assertTrue(e.getResponseError().getMessage().startsWith("Out of Range"));
    }


    @Test
    void whenUpdateCartAndCartItemAndDeltaIsPositiveThenCartItemIsReturned() {
        Product p = new Product();
        p.setId(1L);
        p.setName("test");
        p.setPrice(new BigDecimal(10));
        p.setWarehouse(new Warehouse(1L, 5L));

        Cart c = new Cart(1L);
        c.setTotal(new BigDecimal(30));

        CartItem ci = new CartItem(1L);
        ci.setProduct(p);
        ci.setCart(c);
        ci.setQuantity(3);

        Mockito.when(externalCaller.callGET(any(), eq(Product.class))).thenReturn(p);
        Mockito.when(cartService.findByUser(any())).thenReturn(c);
        Mockito.when(cartItemRepository.save(any())).thenReturn(new CartItem(1L));
        Mockito.when(cartItemRepository.findByCart_IdAndProduct_Id(eq(c.getId()), eq(p.getId()))).thenReturn(Optional.of(ci));

        Assertions.assertEquals(new BigDecimal(30), c.getTotal());
        ModifyCartReqDto dto = new ModifyCartReqDto(1L, 5);
        ci = cartItemService.updateCart(dto,1L);

        Assertions.assertEquals(1L, ci.getId());

        Assertions.assertEquals(new BigDecimal(50), c.getTotal());

    }

    @Test
    void whenUpdate_CartItem_AndDeltaIsNegative_ThenCartItemIsReturned() {
        Product p = new Product();
        p.setId(1L);
        p.setName("test");
        p.setPrice(new BigDecimal(10));
        p.setWarehouse(new Warehouse(1L, 5L));

        Cart c = new Cart(1L);
        c.setTotal(new BigDecimal(30));

        CartItem ci = new CartItem(1L);
        ci.setProduct(p);
        ci.setCart(c);
        ci.setQuantity(3);

        Mockito.when(externalCaller.callGET(any(), eq(Product.class))).thenReturn(p);
        Mockito.when(cartService.findByUser(any())).thenReturn(c);
        Mockito.when(cartItemRepository.save(any())).thenReturn(new CartItem(1L));
        Mockito.when(cartItemRepository.findByCart_IdAndProduct_Id(eq(c.getId()), eq(p.getId()))).thenReturn(Optional.of(ci));

        Assertions.assertEquals(new BigDecimal(30), c.getTotal());
        ModifyCartReqDto dto = new ModifyCartReqDto(1L, 1);
        ci = cartItemService.updateCart(dto,1L);

        Assertions.assertEquals(1L, ci.getId());

        Assertions.assertEquals(new BigDecimal(10), c.getTotal());

    }

    @Test
    void whenUpdate_CartItem_AndDeltaIsZero_ThenCartItemIsReturned() {
        Product p = new Product();
        p.setId(1L);
        p.setName("test");
        p.setPrice(new BigDecimal(10));
        p.setWarehouse(new Warehouse(1L, 5L));

        Cart c = new Cart(1L);
        c.setTotal(new BigDecimal(30));

        CartItem ci = new CartItem(1L);
        ci.setProduct(p);
        ci.setCart(c);
        ci.setQuantity(3);

        Mockito.when(externalCaller.callGET(any(), eq(Product.class))).thenReturn(p);
        Mockito.when(cartService.findByUser(any())).thenReturn(c);
        Mockito.when(cartItemRepository.save(any())).thenReturn(new CartItem(1L));
        Mockito.when(cartItemRepository.findByCart_IdAndProduct_Id(eq(c.getId()), eq(p.getId()))).thenReturn(Optional.of(ci));

        Assertions.assertEquals(new BigDecimal(30), c.getTotal());
        ModifyCartReqDto dto = new ModifyCartReqDto(1L, 3);
        ci = cartItemService.updateCart(dto,1L);

        Assertions.assertEquals(1L, ci.getId());

        Assertions.assertEquals(new BigDecimal(30), c.getTotal());
    }

    @Test
    void whenUpdate_CartItem_AndProductNotExistInCart_ThenApiException() {
        Product p = new Product();
        p.setId(1L);
        p.setName("test");
        p.setPrice(new BigDecimal(10));
        p.setWarehouse(new Warehouse(1L, 5L));

        Cart c = new Cart(1L);
        c.setTotal(new BigDecimal(30));

        Mockito.when(externalCaller.callGET(any(), eq(Product.class))).thenReturn(p);
        Mockito.when(cartService.findByUser(any())).thenReturn(c);
        Mockito.when(cartItemRepository.save(any())).thenReturn(new CartItem(1L));
        Mockito.when(cartItemRepository.findByCart_IdAndProduct_Id(eq(c.getId()), eq(p.getId()))).thenReturn(Optional.empty());

        Assertions.assertEquals(new BigDecimal(30), c.getTotal());
        ModifyCartReqDto dto = new ModifyCartReqDto(1L, 5);
        ApiException e = Assertions.assertThrows(ApiException.class, () -> {
            cartItemService.updateCart(dto,1L);
        });

        Assertions.assertTrue(e.getResponseError().getMessage().endsWith("does not exist in your Cart."));
    }

    @Test
    void whenDelete_CartItem_AndProductExistInCart() {
        Product p = new Product();
        p.setId(1L);
        p.setName("test");
        p.setPrice(new BigDecimal(10));
        p.setWarehouse(new Warehouse(1L, 5L));

        Cart c = new Cart(1L);
        c.setTotal(new BigDecimal(30));

        CartItem ci = new CartItem(1L);
        ci.setProduct(p);
        ci.setCart(c);
        ci.setQuantity(3);

        Mockito.when(externalCaller.callGET(any(), eq(Product.class))).thenReturn(p);
        Mockito.when(cartService.findByUser(any())).thenReturn(c);
        Mockito.when(cartItemRepository.save(any())).thenReturn(new CartItem(1L));
        Mockito.when(cartItemRepository.findByCart_IdAndProduct_Id(eq(c.getId()), eq(p.getId()))).thenReturn(Optional.of(ci));

        Assertions.assertEquals(new BigDecimal(30), c.getTotal());
        cartItemService.deleteFromCart(1L,1L);

        Assertions.assertEquals(1L, ci.getId());
        Assertions.assertEquals(new BigDecimal(0), c.getTotal());
    }

    @Test
    void whenDelete_CartItem_AndProductNotExistInCart_ThenApiException() {
        Product p = new Product();
        p.setId(1L);
        p.setName("test");
        p.setPrice(new BigDecimal(10));
        p.setWarehouse(new Warehouse(1L, 5L));

        Cart c = new Cart(1L);
        c.setTotal(new BigDecimal(30));

        Mockito.when(externalCaller.callGET(any(), eq(Product.class))).thenReturn(p);
        Mockito.when(cartService.findByUser(any())).thenReturn(c);
        Mockito.when(cartItemRepository.save(any())).thenReturn(new CartItem(1L));
        Mockito.when(cartItemRepository.findByCart_IdAndProduct_Id(eq(c.getId()), eq(p.getId()))).thenReturn(Optional.empty());

        Assertions.assertEquals(new BigDecimal(30), c.getTotal());
        ApiException e = Assertions.assertThrows(ApiException.class, () -> {
            cartItemService.deleteFromCart(1L,1L);
        });

        Assertions.assertTrue(e.getResponseError().getMessage().endsWith("does not exist in your Cart."));
    }

    @Test
    void emptyCartAfterPlacedOrder(){

    }

}