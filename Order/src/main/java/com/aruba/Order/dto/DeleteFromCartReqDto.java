package com.aruba.Order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteFromCartReqDto {

    @NotNull
    @Positive
    private Long product_id;

}
