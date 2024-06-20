package com.aruba.Payment.controller;

import com.aruba.Lib.entity.Payment;
import com.aruba.Lib.logging.logger.MsLogger;
import com.aruba.Payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.aruba.Lib.utils.Constant.APP_TOKEN;

@RestController
@RequestMapping(path = "/payments", produces = "application/json")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        MsLogger.logger.info("Get Payment by ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.findById(id));
    }

    @PutMapping(path = "/pay/{id}")
    public ResponseEntity<Payment> pay(@PathVariable Long id,@RequestHeader(APP_TOKEN) Long auth) {
        MsLogger.logger.info("Do Payment with ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.makePayment(id, auth));

    }
}
