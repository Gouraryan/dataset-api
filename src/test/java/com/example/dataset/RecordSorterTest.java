package com.example.dataset;

import com.example.dataset.model.SortOrder;
import com.example.dataset.service.RecordSorter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecordSorterTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RecordSorter sorter = new RecordSorter();

    private JsonNode json(String s) throws Exception { return mapper.readTree(s); }

    @Test
    void sortsNumbersAscending() throws Exception {
        var list = List.of(json("{\"id\":1,\"age\":30}"), json("{\"id\":2,\"age\":25}"), json("{\"id\":3,\"age\":28}"));
        var result = sorter.sort(list, "age", SortOrder.ASC);
        assertEquals(List.of(2, 3, 1), result.stream().map(n -> n.get("id").asInt()).toList());
    }

    @Test
    void sortsDescending() throws Exception {
        var list = List.of(json("{\"id\":1,\"age\":30}"), json("{\"id\":2,\"age\":25}"));
        var result = sorter.sort(list, "age", SortOrder.DESC);
        assertEquals(1, result.get(0).get("id").asInt());
    }

    @Test
    void missingValuesGoLastInBothDirections() throws Exception {
        var list = List.of(json("{\"id\":1}"), json("{\"id\":2,\"age\":25}"));
        assertEquals(2, sorter.sort(list, "age", SortOrder.ASC).get(0).get("id").asInt());
        assertEquals(2, sorter.sort(list, "age", SortOrder.DESC).get(0).get("id").asInt());
    }
}
