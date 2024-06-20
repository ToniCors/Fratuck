package com.aruba.Payment.repository;

import com.aruba.Lib.entity.*;
import com.aruba.LibTest.utils.TestUtils;
import com.aruba.Payment.service.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class RepositoryTest {

    @Autowired
    PaymentService paymentService;


    TestUtils testUtils = new TestUtils();


    @Test
    void findById() {

        Assertions.assertDoesNotThrow(() -> {
            Payment p = paymentService.findById(1L);
            testUtils.writeValueAsString(p);
        });

    }

}
