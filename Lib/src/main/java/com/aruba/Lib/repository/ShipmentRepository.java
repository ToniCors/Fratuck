package com.aruba.Lib.repository;

import com.aruba.Lib.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByOrderId(Long id);

    Optional<Shipment> findByOrder_Id(Long orderID);

}