package com.aruba.Lib.repository;

import com.aruba.Lib.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByProduct_Id(Long id);
    Optional<CartItem> findByCart_Id(Long id);
    Optional<CartItem> findByCart_IdAndProduct_Id(Long cart_Id, Long product_Id);

}
