package com.aruba.Lib.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CheckStockReqDto {

    private Long productID;
    private Long quantity;
}
