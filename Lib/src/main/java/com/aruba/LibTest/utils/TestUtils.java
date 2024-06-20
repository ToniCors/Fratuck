package com.aruba.LibTest.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TestUtils {

    private final ObjectWriter ow;

    public TestUtils() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public void writeValueAsString(Object object) throws JsonProcessingException {
        System.out.println(ow.writeValueAsString(object));
    }
}
