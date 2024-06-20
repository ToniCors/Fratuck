package com.aruba.Payment.service;

import com.aruba.Lib.entity.Payment;
import com.aruba.Lib.enums.PaymentStatus;

public interface PaymentService {

    Payment findById(Long id);

    Payment makePayment(Long paymentId,  Long userId);

    Payment changeStatus(Payment p, PaymentStatus paymentStatus);
}
