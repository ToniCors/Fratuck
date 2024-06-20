package com.aruba.Inventory.mapper;

import com.aruba.Inventory.dto.ProductDTO;
import com.aruba.Lib.entity.Product;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public abstract class ProductMapper {


	public abstract ProductDTO productToProductDTO(Product entity);
	public abstract Product productDTOtoProduct(ProductDTO dto);




}
