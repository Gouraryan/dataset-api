package com.example.dataset.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Groups records by the value of a field. Records missing the field go under "null". */
@Component
public class RecordGrouper {

    public Map<String, List<JsonNode>> group(List<JsonNode> records, String field) {
        Map<String, List<JsonNode>> result = new LinkedHashMap<>();
        for (JsonNode record : records) {
            result.computeIfAbsent(keyOf(record.get(field)), k -> new ArrayList<>()).add(record);
        }
        return result;
    }

    private String keyOf(JsonNode value) {
        if (value == null || value.isNull()) {
            return "null";
        }
        return value.isValueNode() ? value.asText() : value.toString();
    }
}
