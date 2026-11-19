package com.example.dataset.model;

import com.example.dataset.exception.InvalidRequestException;

public enum SortOrder {
    ASC, DESC;

    public static SortOrder from(String value) {
        if (value == null || value.isBlank()) {
            return ASC;
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid order '" + value + "'. Use 'asc' or 'desc'.");
        }
    }
}
