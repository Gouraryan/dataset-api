
package com.example.dataset.service;

import com.example.dataset.dto.InsertRecordResponse;
import com.example.dataset.dto.QueryResponse;
import com.example.dataset.exception.DatasetNotFoundException;
import com.example.dataset.exception.InvalidRequestException;
import com.example.dataset.model.DatasetRecord;
import com.example.dataset.model.SortOrder;
import com.example.dataset.repository.DatasetRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Map.Entry;

@Service
public class DatasetService {

    private final DatasetRecordRepository repository;
    private final ObjectMapper objectMapper;
    private final RecordGrouper grouper;
    private final RecordSorter sorter;

    public DatasetService(
            DatasetRecordRepository repository,
            ObjectMapper objectMapper,
            RecordGrouper grouper,
            RecordSorter sorter) {

        this.repository = repository;
        this.objectMapper = objectMapper;
        this.grouper = grouper;
        this.sorter = sorter;
    }

    @Transactional
    public InsertRecordResponse insert(String datasetName, ObjectNode body) {

        if (body == null || body.isEmpty()) {
            throw new InvalidRequestException(
                    "Record must contain at least one field");
        }

        // Reject null values and empty or whitespace-only strings.
        validateNoNullOrBlank(body, "");

        // Name and department are mandatory strings.
        validateRequiredStringField(body, "name");
        validateRequiredStringField(body, "department");

        // Age must be a non-negative whole number if supplied.
        validateAge(body);

        // Save only after all validations pass.
        DatasetRecord savedRecord =
                repository.save(new DatasetRecord(datasetName, body.toString()));

        JsonNode generatedId =
                objectMapper.valueToTree(savedRecord.getId());

        return new InsertRecordResponse(
                "Record added successfully",
                datasetName,
                generatedId
        );
    }

    private void validateNoNullOrBlank(JsonNode node, String path) {

        if (node == null || node.isNull()) {
            throw new InvalidRequestException(
                    "Null values are not allowed. Invalid field: " + path);
        }

        if (node.isTextual() && node.asText().isBlank()) {
            throw new InvalidRequestException(
                    "Empty or blank values are not allowed. Invalid field: " + path);
        }

        if (node.isObject()) {
            Iterator<Entry<String, JsonNode>> fields = node.fields();

            while (fields.hasNext()) {
                Entry<String, JsonNode> field = fields.next();

                String childPath = path.isEmpty()
                        ? field.getKey()
                        : path + "." + field.getKey();

                validateNoNullOrBlank(field.getValue(), childPath);
            }
        }

        if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                validateNoNullOrBlank(
                        node.get(i),
                        path + "[" + i + "]"
                );
            }
        }
    }

    // Validate mandatory string fields.
    private void validateRequiredStringField(
            ObjectNode body,
            String fieldName) {

        JsonNode value = body.get(fieldName);

        if (value == null) {
            throw new InvalidRequestException(
                    "Field '" + fieldName + "' is required");
        }

        if (!value.isTextual()) {
            throw new InvalidRequestException(
                    "Field '" + fieldName + "' must be a string");
        }

        if (value.asText().isBlank()) {
            throw new InvalidRequestException(
                    "Field '" + fieldName + "' cannot be empty or blank");
        }
    }

    // Validate age: must be a non-negative whole number.
    private void validateAge(ObjectNode body) {

        JsonNode age = body.get("age");

        // Age is optional.
        if (age == null) {
            return;
        }

        if (!age.isIntegralNumber() || !age.canConvertToInt()) {
            throw new InvalidRequestException(
                    "Age must be a valid whole number");
        }

        if (age.intValue() < 0) {
            throw new InvalidRequestException(
                    "Age cannot be negative");
        }
    }

    @Transactional(readOnly = true)
    public QueryResponse query(
            String datasetName,
            String groupBy,
            String sortBy,
            String order) {

        SortOrder sortOrder = SortOrder.from(order);

        List<JsonNode> records = load(datasetName);

        boolean grouping = hasText(groupBy);
        boolean sorting = hasText(sortBy);

        if (sorting) {
            validateSortField(records, sortBy.trim());
        }

        if (grouping) {
            validateGroupField(records, groupBy.trim());
        }

        if (grouping) {

            Map<String, List<JsonNode>> grouped =
                    grouper.group(records, groupBy.trim());

            if (sorting) {
                grouped.replaceAll(
                        (key, list) ->
                                sorter.sort(list, sortBy.trim(), sortOrder)
                );
            }

            return QueryResponse.grouped(grouped);
        }

        if (sorting) {
            return QueryResponse.sorted(
                    sorter.sort(records, sortBy.trim(), sortOrder)
            );
        }

        return QueryResponse.plain(records);
    }

    private List<JsonNode> load(String datasetName) {

        List<DatasetRecord> stored =
                repository.findByDatasetNameOrderByIdAsc(datasetName);

        if (stored.isEmpty()) {
            throw new DatasetNotFoundException(datasetName);
        }

        return stored.stream()
                .map(this::parse)
                .toList();
    }

    private JsonNode parse(DatasetRecord record) {

        try {
            JsonNode original =
                    objectMapper.readTree(record.getPayload());

            ObjectNode result = objectMapper.createObjectNode();

            result.put("id", record.getId());

            if (original.has("name")) {
                result.set("name", original.get("name"));
            }

            if (original.has("age")) {
                result.set("age", original.get("age"));
            }

            if (original.has("department")) {
                result.set("department", original.get("department"));
            }

            return result;

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Corrupt JSON stored for record " + record.getId(), e);
        }
    }

    private void validateSortField(
            List<JsonNode> records,
            String field) {

        boolean fieldExists = records.stream()
                .anyMatch(record -> record.has(field));

        if (!fieldExists) {
            throw new InvalidRequestException(
                    "Invalid sortBy field '" + field
                    + "'. Available fields: id, name, age, department"
            );
        }
    }

    private void validateGroupField(
            List<JsonNode> records,
            String field) {

        boolean fieldExists = records.stream()
                .anyMatch(record -> record.has(field));

        if (!fieldExists) {
            throw new InvalidRequestException(
                    "Invalid groupBy field '" + field
                    + "'. Available fields: id, name, age, department"
            );
        }
    }

    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
