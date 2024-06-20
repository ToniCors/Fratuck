package com.aruba.Inventory.service;

import com.aruba.Inventory.dto.RefreshStockDtoReq;
import com.aruba.Lib.dto.OrderProductCount;
import com.aruba.Lib.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {

    Warehouse findById(Long id);

    Page<Warehouse> findAll(Pageable pageable);

    Warehouse refreshStock(RefreshStockDtoReq req);

    boolean checkStock(List<OrderProductCount> req);
}
