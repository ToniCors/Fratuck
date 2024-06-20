package com.aruba.Inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshStockDtoReq {

    private Long id;

    private Long quantity;

    private int operation;

}
