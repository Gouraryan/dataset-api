package com.example.dataset.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record InsertRecordResponse(String message, String dataset, JsonNode recordId) { }
