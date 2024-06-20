package com.aruba.Delivery.service;

import com.aruba.Delivery.dto.CreateShipmentReqDto;
import com.aruba.Lib.entity.Shipment;
import com.aruba.Lib.enums.ShipmentStatus;

public interface ShipmentService {

    Shipment findById(Long id);

    Shipment findByOrderId(Long id);

    Shipment createShipment(CreateShipmentReqDto req);

    Shipment progressShipment(Long req);

    Shipment deliverShipment(Long req);

    Shipment changeStatus(Shipment s, ShipmentStatus newStatus);
}
