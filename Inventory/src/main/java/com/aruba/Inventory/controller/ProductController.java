package com.aruba.Inventory.controller;


import com.aruba.Inventory.dto.ProductDTO;
import com.aruba.Inventory.service.ProductService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/products", produces = "application/json")
public class ProductController {

    @Autowired
    private ProductService service;

        @GetMapping(path = "/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        MsLogger.logger.info("Get Product by ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(service.findById(id));

    }

    @GetMapping
    @Operation(parameters = {@Parameter(in = ParameterIn.QUERY, description = "Page you want to retrieve (0..N)", name = "page", content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))), @Parameter(in = ParameterIn.QUERY, description = "Number of records per page.", name = "size", content = @Content(schema = @Schema(type = "integer", defaultValue = "20"))), @Parameter(in = ParameterIn.QUERY, description = "Sorting criteria in the format: property(,asc|desc). " + "Default sort order is ascending. " + "Multiple sort criteria are supported.", name = "sort", content = @Content(array = @ArraySchema(schema = @Schema(type = "string"))))})
    public ResponseEntity<Page<ProductDTO>> findAll(@Parameter(hidden = true) Pageable pageable) {
        MsLogger.logger.info("Get All Products");

        return ResponseEntity.status(HttpStatus.OK).body(service.findAll(pageable));
    }


//    @DeleteMapping(path = "/{id}")
//    public ResponseEntity<ProductDTO> deleteById(@PathVariable int id) {
//        log.info("Delete Product by ID: {}", id);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
//
//    }

//    @PutMapping
//    @Validated
//    public ResponseEntity<ProductDTO> update(@Valid @RequestBody ProductDTO product) {
//        log.info("Update Product: {}", product.toString());
//        return ResponseEntity.status(HttpStatus.OK).body(service.update(product));
//
//    }

//    @PostMapping
//    @Validated
//    public ResponseEntity<ProductDTO> create(@Valid@RequestBody CreateProductDTO product) {
//        log.info("Create Product: {}", product.toString());
//        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(product));
//
//    }



}
