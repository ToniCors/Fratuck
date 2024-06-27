package com.aruba.Inventory.service.impl;

import com.aruba.Inventory.dto.ProductDTO;
import com.aruba.Inventory.mapper.ProductMapper;
import com.aruba.Inventory.service.ProductService;
import com.aruba.Lib.entity.Product;
import com.aruba.Lib.exception.ApiException;
import com.aruba.Lib.exception.ErrorCodes;
import com.aruba.Lib.exception.ResponseError;
import com.aruba.Lib.repository.ProductRepository;
import com.aruba.Lib.service.ExternalCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductMapper mapper;

    @Autowired
    private ExternalCaller caller;

    @Override
    public ProductDTO findById(Long id) {

        Optional<Product> res = repository.findById(id);

        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder().
					httpStatus(HttpStatus.NOT_FOUND).
					message(String.format("Product with id {%s} was not found.", id)).
					errorCodes(ErrorCodes.ENTITY_NOT_FOUND).
					build());
        }

        return mapper.productToProductDTO(res.get());
    }

    @Override
    public Page<ProductDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(product -> mapper.productToProductDTO(product));

    }

}
