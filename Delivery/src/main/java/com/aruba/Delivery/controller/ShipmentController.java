package com.aruba.Delivery.controller;

import com.aruba.Delivery.dto.CreateShipmentReqDto;
import com.aruba.Delivery.service.ShipmentService;
import com.aruba.Lib.entity.Shipment;
import com.aruba.Lib.logging.logger.MsLogger;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/shipment", produces = "application/json")
public class ShipmentController {

    @Autowired
    private ShipmentService service;

    @GetMapping(path = "/{id}")
    public ResponseEntity<Shipment> getById(@PathVariable Long id) {
        MsLogger.logger.info("Get Shipment by ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(service.findById(id));
    }

    @GetMapping(path = "/order/{orderID}")
    public ResponseEntity<Shipment> getByOrderId(@PathVariable Long orderID) {
        MsLogger.logger.info("Get Shipment by orderID: {}", orderID);
        return ResponseEntity.status(HttpStatus.OK).body(service.findByOrderId(orderID));
    }

    @PutMapping(path = "/create")
    public ResponseEntity<Shipment> createShipment(@Valid @RequestBody CreateShipmentReqDto req) {
        MsLogger.logger.info("Create Shipment for orderId: {}", req.getOrderID());
        return ResponseEntity.status(HttpStatus.OK).body(service.createShipment(req));
    }

    @PostMapping(path = "/progress/{id}")
    public ResponseEntity<Shipment> progressShipment(@PathVariable Long id) {
        MsLogger.logger.info("Progress Shipment for orderId: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(service.progressShipment(id));
    }

    @PostMapping(path = "/deliver/{id}")
    public ResponseEntity<Shipment> deliverShipment(@PathVariable Long id) {
        MsLogger.logger.info("Deliver Shipment for orderId: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(service.deliverShipment(id));
    }
}
