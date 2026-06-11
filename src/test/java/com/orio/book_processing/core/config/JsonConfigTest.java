package com.orio.book_processing.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class JsonConfigTest {

    @Test
    void objectMapperBean_isCreated() {
        assertNotNull(new JsonConfig().objectMapper());
    }

    @Test
    void objectMapperBean_readsAndWritesJson() throws JsonProcessingException {
        ObjectMapper mapper = new JsonConfig().objectMapper();
        record Payload(int n, String s) {
        }
        Payload original = new Payload(2, "x");
        String json = mapper.writeValueAsString(original);
        Payload roundTrip = mapper.readValue(json, Payload.class);
        assertEquals(original, roundTrip);
    }
}
