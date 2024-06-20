package com.aruba.Payment.controller;

import com.aruba.Lib.entity.Payment;
import com.aruba.Lib.enums.PaymentStatus;
import com.aruba.Lib.exception.ApiException;
import com.aruba.Lib.exception.ResponseError;
import com.aruba.Lib.logging.logger.MsLogger;
import com.aruba.Payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.aruba.Lib.utils.Constant.APP_TOKEN;

@RestController
@RequestMapping(path = "/payments", produces = "application/json")
public class PaymentController {

    @Autowired
    private PaymentService service;

    @GetMapping(path = "/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        MsLogger.logger.info("Get Payment by ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(service.findById(id));

    }

    @PostMapping(path = "pay/{id}")
    public ResponseEntity<?> makePayment(@PathVariable Long id, @RequestHeader(APP_TOKEN) Long auth) {
        MsLogger.logger.info("User {} Make Payment ID: {}", auth, id);
        Payment p = service.makePayment(id, auth);
        if (p.getStatus().equals(PaymentStatus.SUCCESSFUL))
            return ResponseEntity.status(HttpStatus.OK).body(p);
        else
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.BAD_REQUEST).message("Enable to make payment. One or more product is out of stock").build());


    }

}
