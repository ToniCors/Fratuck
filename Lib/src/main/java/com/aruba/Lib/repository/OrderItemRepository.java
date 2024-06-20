package com.aruba.Lib.repository;

import com.aruba.Lib.dto.OrderProductCount;
import com.aruba.Lib.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query(value = "SELECT new com.aruba.Lib.dto.OrderProductCount(oi.product.id, COUNT(oi.product.id)) "
            + "FROM OrderItem AS oi WHERE oi.order.id = ?1 GROUP BY oi.product.id")
    List<OrderProductCount> groupByProduct_id(Long orderId);
}
