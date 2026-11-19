package com.example.dataset.model;

import jakarta.persistence.*;
import java.time.Instant;

/** One JSON record belonging to a named dataset. The JSON is stored in a MySQL JSON column. */
@Entity
@Table(name = "dataset_records", indexes = @Index(name = "idx_dataset_name", columnList = "dataset_name"))
public class DatasetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dataset_name", nullable = false, length = 64)
    private String datasetName;

    @Column(nullable = false, columnDefinition = "json")
    private String payload;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected DatasetRecord() { }

    public DatasetRecord(String datasetName, String payload) {
        this.datasetName = datasetName;
        this.payload = payload;
    }

    public Long getId() { return id; }
    public String getDatasetName() { return datasetName; }
    public String getPayload() { return payload; }
    public Instant getCreatedAt() { return createdAt; }
}
