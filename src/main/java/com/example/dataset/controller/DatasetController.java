package com.example.dataset.controller;

import com.example.dataset.dto.InsertRecordResponse;
import com.example.dataset.dto.QueryResponse;
import com.example.dataset.service.DatasetService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dataset/{datasetName}")
@Validated
public class DatasetController {

    private static final String NAME_REGEX = "^[A-Za-z0-9_-]{1,64}$";
    private static final String NAME_MSG = "datasetName must be 1-64 chars: letters, digits, '_' or '-'";

    private final DatasetService service;

    public DatasetController(DatasetService service) {
        this.service = service;
    }

    @PostMapping("/record")
    public ResponseEntity<InsertRecordResponse> insert(
            @PathVariable @Pattern(regexp = NAME_REGEX, message = NAME_MSG) String datasetName,
            @RequestBody ObjectNode body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.insert(datasetName, body));
    }

    @GetMapping("/query")
    public QueryResponse query(
            @PathVariable @Pattern(regexp = NAME_REGEX, message = NAME_MSG) String datasetName,
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        return service.query(datasetName, groupBy, sortBy, order);
    }
}
