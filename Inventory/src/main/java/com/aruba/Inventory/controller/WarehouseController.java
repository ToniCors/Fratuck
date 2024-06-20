package com.aruba.Inventory.controller;

import com.aruba.Inventory.dto.RefreshStockDtoReq;
import com.aruba.Inventory.service.WarehouseService;
import com.aruba.Lib.dto.OrderProductCount;
import com.aruba.Lib.entity.Warehouse;
import com.aruba.Lib.logging.logger.MsLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/warehouse", produces = "application/json")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<Warehouse> getById(@PathVariable Long id) {
        MsLogger.logger.info("Get Product by ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(warehouseService.findById(id));

    }

    @GetMapping
    @Operation(parameters = {@Parameter(in = ParameterIn.QUERY, description = "Page you want to retrieve (0..N)", name = "page", content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))), @Parameter(in = ParameterIn.QUERY, description = "Number of records per page.", name = "size", content = @Content(schema = @Schema(type = "integer", defaultValue = "20"))), @Parameter(in = ParameterIn.QUERY, description = "Sorting criteria in the format: property(,asc|desc). " + "Default sort order is ascending. " + "Multiple sort criteria are supported.", name = "sort", content = @Content(array = @ArraySchema(schema = @Schema(type = "string"))))})
    public ResponseEntity<Page<Warehouse>> findAll(@Parameter(hidden = true) Pageable pageable) {
        MsLogger.logger.info("Get All Products");

        return ResponseEntity.status(HttpStatus.OK).body(warehouseService.findAll(pageable));
    }

    @PostMapping(path = "/refreshStock")
    public ResponseEntity<Warehouse> refreshStock(@RequestBody RefreshStockDtoReq req) {
        MsLogger.logger.info("Refresh Stock by Product ID: {}", req);
        return ResponseEntity.status(HttpStatus.OK).body(warehouseService.refreshStock(req));

    }

    @PostMapping(path = "/checkStock")
    public ResponseEntity<?> checkStock(@RequestBody List<OrderProductCount> req) {
        MsLogger.logger.info("Check Stock By CartID ID: {}", req);

        return ResponseEntity.status(warehouseService.checkStock(req)?
                HttpStatus.OK :
                HttpStatus.NO_CONTENT).build();

    }


}
