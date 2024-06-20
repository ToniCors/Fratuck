package com.aruba.Inventory.repository;

import com.aruba.Inventory.dto.ProductDTO;
import com.aruba.Inventory.service.ProductService;
import com.aruba.Inventory.service.UserService;
import com.aruba.Inventory.service.WarehouseService;
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
    private ProductService productService;

    @Autowired
    private WarehouseService warehouseService;

    TestUtils testUtils = new TestUtils();
    @Autowired
    private UserService userService;

    @Test
    void findById() {

        Assertions.assertDoesNotThrow(() -> {
            ProductDTO p = productService.findById(1L);
            testUtils.writeValueAsString(p);
        });

        Assertions.assertDoesNotThrow(() -> {
            Warehouse p = warehouseService.findById(1L);
            testUtils.writeValueAsString(p);
        });


        Assertions.assertDoesNotThrow(() -> {
            User u = userService.findById(1L);
            testUtils.writeValueAsString(u);
        });


    }

}
