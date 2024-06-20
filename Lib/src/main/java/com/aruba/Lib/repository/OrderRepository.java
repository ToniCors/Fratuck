package com.aruba.Lib.repository;

import com.aruba.Lib.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order>  findByUser_Id(Long id, Pageable pageable);
}
