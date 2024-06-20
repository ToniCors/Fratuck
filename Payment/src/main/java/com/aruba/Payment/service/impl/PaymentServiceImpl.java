package com.aruba.Payment.service.impl;

import com.aruba.Lib.component.ConfigProperties;
import com.aruba.Lib.dto.OrderProductCount;
import com.aruba.Lib.entity.Order;
import com.aruba.Lib.entity.Payment;
import com.aruba.Lib.enums.OrderStatus;
import com.aruba.Lib.enums.PaymentStatus;
import com.aruba.Lib.exception.ApiException;
import com.aruba.Lib.exception.ErrorCodes;
import com.aruba.Lib.exception.ResponseError;
import com.aruba.Lib.repository.PaymentRepository;
import com.aruba.Lib.service.ExternalCaller;
import com.aruba.Lib.service.MessageSender;
import com.aruba.Payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private ExternalCaller externalCaller;

    @Autowired
    private ConfigProperties config;

    @Autowired
    private MessageSender messageSender;

    @Override
    public Payment findById(Long id) {
        Optional<Payment> res = repository.findById(id);

        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("Entity with id {%s} was not found.", id)).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }

        return res.get();
    }

    @Override
    public Payment makePayment(Long paymentId, Long userId) {

        Payment p = this.findById(paymentId);

        Order o = p.getOrder();
        if (o.getUser().getId().equals(userId)) {
            if (p.getStatus() == PaymentStatus.AWAITING) {
                if (!checkStock(o.getId())) {
                    p.setStatus(PaymentStatus.UNSUCCESSFUL);
                    p.getOrder().setStatus(OrderStatus.CANCELLED);
                    return repository.save(p);
                } else {
                    p.setStatus(PaymentStatus.SUCCESSFUL);
                    p.getOrder().setStatus(OrderStatus.PAYD);

                    p = repository.save(p);
                    messageSender.sendMessage(p);
                    return p;
                }
            } else {
                throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.BAD_REQUEST).message(String.format("Enable to make payment. Status is {%s}", p.getStatus())).build());
            }
        } else
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.BAD_REQUEST).message(String.format("Enable to make payment. This is not order of userId {%s} for payment id {%s}", userId, paymentId)).build());

    }


    @Override
    public Payment changeStatus(Payment p, PaymentStatus paymentStatus) {
        p.setStatus(paymentStatus);
        return repository.save(p);
    }

    private boolean checkStock(Long orderId) {
        String oUrl = "%s%s/orderItems/group/product/%s".formatted(config.getApiGatewayHost(), config.getOrderBasePath(), orderId);
        OrderProductCount[] productCounts = externalCaller.callGET(oUrl, OrderProductCount[].class);

        String pUrl = "%s%s/warehouse/checkStock".formatted(config.getApiGatewayHost(), config.getInventoryBasePath());
        ResponseEntity<OrderProductCount[]> checkStock = externalCaller.exchange(pUrl, HttpMethod.POST, new HttpEntity<>(productCounts), OrderProductCount[].class);

        return checkStock.getStatusCode().equals(HttpStatus.OK);
    }

}
