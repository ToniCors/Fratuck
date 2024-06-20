package com.aruba.Lib.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderProductCount {

    private  Long product_id;
    private  Long total;

    public OrderProductCount() {
    }

    public OrderProductCount(Long product_id, Long total) {
        this.product_id = product_id;
        this.total = total;
    }

}
