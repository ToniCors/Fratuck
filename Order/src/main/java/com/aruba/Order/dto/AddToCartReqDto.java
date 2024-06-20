package com.aruba.Order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartReqDto {

    @NotNull
    private Long product_id;

    @NotNull
    @Min(1)
    private Integer quantity;
}
