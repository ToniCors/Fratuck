package com.aruba.Inventory.service.impl;

import com.aruba.Inventory.dto.RefreshStockDtoReq;
import com.aruba.Inventory.service.ProductService;
import com.aruba.Inventory.service.WarehouseService;
import com.aruba.Lib.dto.OrderProductCount;
import com.aruba.Lib.entity.Warehouse;
import com.aruba.Lib.exception.ApiException;
import com.aruba.Lib.exception.ErrorCodes;
import com.aruba.Lib.exception.ResponseError;
import com.aruba.Lib.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseRepository repository;

    @Autowired
    private ProductService productService;

    @Override
    public Warehouse findById(Long id) {

        Optional<Warehouse> res = repository.findById(id);

        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("Entity with id {%s} was not found.", id)).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }

        return res.get();
    }

    @Override
    public Page<Warehouse> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Warehouse refreshStock(RefreshStockDtoReq req) {
        Optional<Warehouse> ow = repository.findById(req.getId());
        long newStock;
        if (ow.isPresent()) {
            Warehouse w = ow.get();
            if (req.getOperation() == 0) {
                newStock = w.getStock() + req.getQuantity();
                w.setStock(newStock);
            } else {
                newStock = w.getStock() - req.getQuantity();
                if (newStock<0) newStock =0L;
                w.setStock(newStock);
            }
            return repository.save(w);
        } else {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("Warehouse with id {%s} was not found.", req.getId())).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }
    }

    @Override
    public boolean checkStock(List<OrderProductCount> req) {

        for (OrderProductCount dto : req) {

            Warehouse w = this.findById(dto.getProduct_id());
            if (dto.getTotal() > w.getStock()) {
                return false;
            }

        }
        return true;
    }
}
