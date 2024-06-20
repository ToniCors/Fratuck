package com.aruba.Delivery.service.impl;


import com.aruba.Delivery.dto.CreateShipmentReqDto;
import com.aruba.Delivery.service.ShipmentService;
import com.aruba.Lib.component.ConfigProperties;
import com.aruba.Lib.entity.Order;
import com.aruba.Lib.entity.Shipment;
import com.aruba.Lib.enums.OrderStatus;
import com.aruba.Lib.enums.ShipmentStatus;
import com.aruba.Lib.exception.ApiException;
import com.aruba.Lib.exception.ErrorCodes;
import com.aruba.Lib.exception.ResponseError;
import com.aruba.Lib.logging.logger.MsLogger;
import com.aruba.Lib.repository.ShipmentRepository;
import com.aruba.Lib.service.ExternalCaller;
import com.aruba.Lib.service.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    @Autowired
    private ShipmentRepository repository;

    @Autowired
    private ExternalCaller externalCaller;

    @Autowired
    private ConfigProperties config;

    @Autowired
    private MessageSender messageSender;

    @Override
    public Shipment findById(Long id) {
        Optional<Shipment> res = repository.findById(id);

        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("Shipment with id {%s} was not found.", id)).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }

        return res.get();
    }

    @Override
    public Shipment findByOrderId(Long id) {
        Optional<Shipment> res = repository.findByOrderId(id);

        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.NOT_FOUND).message(String.format("Shipment with id {%s} was not found.", id)).errorCodes(ErrorCodes.ENTITY_NOT_FOUND).build());
        }

        return res.get();
    }

    @Override
    public Shipment createShipment(CreateShipmentReqDto req) {
        Optional<Shipment> res = repository.findByOrderId(req.getOrderID());
        if (res.isPresent()) {
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.BAD_REQUEST).message(String.format("Shipment for Order with id {%s} already exist.", req.getOrderID())).build());
        }
        Shipment shipment = repository.save(new Shipment(null, new Order(req.getOrderID()), req.getNote()));
        MsLogger.logger.info("New Shipment Created {} ", shipment.getId());
        return shipment;
    }

    @Override
    public Shipment progressShipment(Long id) {
        MsLogger.logger.info("progress Shipment {} ", id);
        Shipment shipment = this.findByOrderId(id);


        if (shipment.getStatus().equals(ShipmentStatus.NEW)) {
            shipment.setStatus(ShipmentStatus.PROGRESS);
            shipment.getOrder().setStatus(OrderStatus.PREPARE_SHIPMENT);
            return repository.save(shipment);
        } else
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.BAD_REQUEST).message(String.format("Status for Order with id {%s} is not NEW.", id)).build());

    }

    @Override
    public Shipment deliverShipment(Long id) {
        MsLogger.logger.info("deliver Shipment {} ", id);

        Shipment shipment = this.findByOrderId(id);
        if (shipment.getStatus().equals(ShipmentStatus.PROGRESS)) {
            shipment.setStatus(ShipmentStatus.SHIPPED);
            shipment.getOrder().setStatus(OrderStatus.SHIPPED);
            Shipment s = repository.save(shipment);
            messageSender.sendMessage(s);
            return s;
        } else
            throw new ApiException(ResponseError.builder().httpStatus(HttpStatus.BAD_REQUEST).message(String.format("Status for Order with id {%s} is not PROGRESS.", id)).build());

    }

    @Override
    public Shipment changeStatus(Shipment s, ShipmentStatus newStatus) {
        s.setStatus(newStatus);
        Shipment shipment = repository.save(s);
        MsLogger.logger.info("Status Changed {} ", newStatus.name());
        return shipment;
    }

}
