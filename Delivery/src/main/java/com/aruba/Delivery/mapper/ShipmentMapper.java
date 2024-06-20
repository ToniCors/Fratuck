package com.aruba.Delivery.mapper;

import com.aruba.Delivery.dto.ShipmentResDto;
import com.aruba.Lib.entity.Shipment;

public abstract class ShipmentMapper {

    public abstract ShipmentResDto shipmentToShipmentDTO(Shipment entity);
    public abstract Shipment shipmentResDTOtoShipment(ShipmentResDto dto);
}
