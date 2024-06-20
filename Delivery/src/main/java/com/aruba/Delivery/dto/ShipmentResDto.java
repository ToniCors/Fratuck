package com.aruba.Delivery.dto;

import com.aruba.Lib.entity.Order;
import com.aruba.Lib.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResDto {


    private Long id;
    private Order order;

    private ShipmentStatus status;

    private String note;

    private Timestamp created;

    private Timestamp modified;
}
