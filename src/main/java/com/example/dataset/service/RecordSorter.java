package com.example.dataset.service;

import com.example.dataset.model.SortOrder;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/** Sorts records by a field. Numbers compare numerically, everything else as case-insensitive text. Missing values go last. */
@Component
public class RecordSorter {

    public List<JsonNode> sort(List<JsonNode> records, String field, SortOrder order) {
        Comparator<JsonNode> byValue = (a, b) -> compareValues(a.get(field), b.get(field));
        if (order == SortOrder.DESC) {
            byValue = byValue.reversed();
        }
        Comparator<JsonNode> missingLast = Comparator.comparing((JsonNode r) -> isMissing(r.get(field)));
        return records.stream().sorted(missingLast.thenComparing(byValue)).toList();
    }

    private boolean isMissing(JsonNode n) {
        return n == null || n.isNull();
    }

    private int compareValues(JsonNode a, JsonNode b) {
        if (isMissing(a) || isMissing(b)) {
            return 0;
        }
        if (a.isNumber() && b.isNumber()) {
            return a.decimalValue().compareTo(b.decimalValue());
        }
        return a.asText().compareToIgnoreCase(b.asText());
    }
}
