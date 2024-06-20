package com.aruba.Delivery.repository;

import com.aruba.Delivery.service.ShipmentService;
import com.aruba.Lib.entity.*;
import com.aruba.LibTest.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class RepositoryTest {

    @Autowired
    ShipmentService shipmentService;


    TestUtils testUtils = new TestUtils();


    @Test
    void findById() {

        Assertions.assertDoesNotThrow(() -> {
            Shipment p = shipmentService.findById(1L);
            testUtils.writeValueAsString(p);
        });

    }

}
