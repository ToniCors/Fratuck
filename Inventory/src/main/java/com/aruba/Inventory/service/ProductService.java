package com.aruba.Inventory.service;

import com.aruba.Inventory.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductService {

	ProductDTO findById(Long id);
    Page<ProductDTO> findAll(Pageable pageable);

}
