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
import com.aruba.Lib.logging.logger.MsLogger;
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

import java.lang.reflect.Array;
import java.util.Arrays;
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
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("Payment with id {%s} was not found.", id)).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }

        return res.get();
    }

    @Override
    public Payment makePayment(Long paymentId, Long userId) {
        MsLogger.logger.info("Make payment. Payment ID {}, user ID {}", paymentId, userId);
        Payment p = this.findById(paymentId);

        Order o = p.getOrder();
        if (o.getUser().getId().equals(userId)) {
            if (p.getStatus() == PaymentStatus.AWAITING) {
                OrderProductCount[] productCounts = getOrderProductCount(o.getId());
                if (!checkStock(productCounts)) {
                    p.setStatus(PaymentStatus.UNSUCCESSFUL);
                    p.getOrder().setStatus(OrderStatus.CANCELLED);
                    return repository.save(p);
                } else {
                    p.setStatus(PaymentStatus.SUCCESSFUL);
                    p.getOrder().setStatus(OrderStatus.PAYD);

                    p = repository.save(p);
                    boolean updated = updateStock(productCounts);
                    MsLogger.logger.info("Update stock status: {}", updated);

                    messageSender.sendMessage(o);
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
        MsLogger.logger.info("Change payment Status. Payment ID {}, new status {}", p.getId(), paymentStatus.name());

        p.setStatus(paymentStatus);
        return repository.save(p);
    }

    private OrderProductCount[] getOrderProductCount(Long orderId) {
        MsLogger.logger.info("Get order product for order ID {}", orderId);

        String oUrl = "%s%s/orderItems/group/product/%s".formatted(config.getApiGatewayHost(), config.getOrderBasePath(), orderId);
        return externalCaller.callGET(oUrl, OrderProductCount[].class);
    }

    private boolean checkStock(OrderProductCount[] productCounts) {
        MsLogger.logger.info("Check Stock. Products {}", Arrays.toString(productCounts));

        String pUrl = "%s%s/warehouse/checkStock".formatted(config.getApiGatewayHost(), config.getInventoryBasePath());
        ResponseEntity<OrderProductCount[]> checkStock = externalCaller.exchange(pUrl, HttpMethod.POST, new HttpEntity<>(productCounts), OrderProductCount[].class);

        return checkStock.getStatusCode().equals(HttpStatus.OK);
    }

    private boolean updateStock(OrderProductCount[] productCounts) {
        MsLogger.logger.info("Update Stock. Products {}", Arrays.toString(productCounts));

        String pUrl = "%s%s/warehouse/updateStock".formatted(config.getApiGatewayHost(), config.getInventoryBasePath());
        ResponseEntity<OrderProductCount[]> checkStock = externalCaller.exchange(pUrl, HttpMethod.POST, new HttpEntity<>(productCounts), OrderProductCount[].class);

        return checkStock.getStatusCode().equals(HttpStatus.OK);
    }


}
