package com.aruba.Lib.repository;

import com.aruba.Lib.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser_Id(Long id);
    Optional<Cart> findByIdAndUser_Id(Long id, Long userId);

    @Modifying(clearAutomatically = true)
    @Query(value = "update cart c set c.total = (select sum(ci.quantity*p.price) from cart_items ci, product p where ci.cart_id=?1) where c.id = ?1", nativeQuery = true )
    Integer updateTotal(Long id);


}
