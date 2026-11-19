package com.example.dataset;

import com.example.dataset.service.RecordGrouper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordGrouperTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RecordGrouper grouper = new RecordGrouper();

    private JsonNode json(String s) throws Exception { return mapper.readTree(s); }

    @Test
    void groupsByField() throws Exception {
        var list = List.of(json("{\"id\":1,\"department\":\"Engineering\"}"),
                json("{\"id\":2,\"department\":\"Engineering\"}"),
                json("{\"id\":3,\"department\":\"Marketing\"}"));
        var result = grouper.group(list, "department");
        assertEquals(2, result.get("Engineering").size());
        assertEquals(1, result.get("Marketing").size());
    }

    @Test
    void recordsWithoutFieldGoUnderNull() throws Exception {
        var result = grouper.group(List.of(json("{\"id\":1}")), "department");
        assertTrue(result.containsKey("null"));
    }
}
