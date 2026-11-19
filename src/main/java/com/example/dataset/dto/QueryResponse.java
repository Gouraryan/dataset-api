package com.example.dataset.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/** Only the field relevant to the requested operation is serialized. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record QueryResponse(
        Map<String, List<JsonNode>> groupedRecords,
        List<JsonNode> sortedRecords,
        List<JsonNode> records) {

    public static QueryResponse grouped(Map<String, List<JsonNode>> grouped) {
        return new QueryResponse(grouped, null, null);
    }

    public static QueryResponse sorted(List<JsonNode> sorted) {
        return new QueryResponse(null, sorted, null);
    }

    public static QueryResponse plain(List<JsonNode> records) {
        return new QueryResponse(null, null, records);
    }
}
