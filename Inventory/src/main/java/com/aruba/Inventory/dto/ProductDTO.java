package com.aruba.Inventory.dto;

import com.aruba.Lib.entity.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

	private Long id;

	private String name;

	private String description;

	private BigDecimal price;

	private Timestamp created;

	private Timestamp modified;

	private Warehouse warehouse;

}
