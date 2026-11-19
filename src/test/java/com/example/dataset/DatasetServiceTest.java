
package com.example.dataset;

import com.example.dataset.dto.QueryResponse;
import com.example.dataset.exception.DatasetNotFoundException;
import com.example.dataset.exception.InvalidRequestException;
import com.example.dataset.model.DatasetRecord;
import com.example.dataset.repository.DatasetRecordRepository;
import com.example.dataset.service.DatasetService;
import com.example.dataset.service.RecordGrouper;
import com.example.dataset.service.RecordSorter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DatasetServiceTest {

    private DatasetRecordRepository repository;
    private DatasetService service;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        repository = mock(DatasetRecordRepository.class);
        mapper = new ObjectMapper();

        service = new DatasetService(
                repository,
                mapper,
                new RecordGrouper(),
                new RecordSorter()
        );
    }

    private ObjectNode body(String json) throws Exception {
        return (ObjectNode) mapper.readTree(json);
    }

    private void assertRejected(String json) throws Exception {
        assertThrows(
                InvalidRequestException.class,
                () -> service.insert("employees", body(json))
        );

        verify(repository, never()).save(any());
    }

    // Create real DatasetRecord objects for query tests.
    private DatasetRecord storedRecord(Long id, String payload) {
        DatasetRecord record =
                new DatasetRecord("employees", payload);

        ReflectionTestUtils.setField(record, "id", id);

        return record;
    }

    // REJECTION TESTS

    @Test
    void rejectsMissingName() throws Exception {
        assertRejected("""
                {"department": "Engineering"}
                """);
    }

    @Test
    void rejectsMissingDepartment() throws Exception {
        assertRejected("""
                {"name": "Rahul"}
                """);
    }

    @Test
    void rejectsNonStringName() throws Exception {
        assertRejected("""
                {"name": 123, "department": "Engineering"}
                """);
    }

    @Test
    void rejectsNonStringDepartment() throws Exception {
        assertRejected("""
                {"name": "Rahul", "department": 123}
                """);
    }

    @Test
    void rejectsNullValues() throws Exception {
        assertRejected("""
                {"name": "Rahul", "department": "Engineering", "city": null}
                """);
    }

    @Test
    void rejectsBlankStrings() throws Exception {
        assertRejected("""
                {"name": "Rahul", "department": "Engineering", "city": "   "}
                """);
    }

    @Test
    void rejectsNegativeAge() throws Exception {
        assertRejected("""
                {"name": "Rahul", "department": "Engineering", "age": -5}
                """);
    }

    @Test
    void rejectsDecimalAge() throws Exception {
        assertRejected("""
                {"name": "Rahul", "department": "Engineering", "age": 25.5}
                """);
    }

    @Test
    void rejectsStringAge() throws Exception {
        assertRejected("""
                {"name": "Rahul", "department": "Engineering", "age": "twenty"}
                """);
    }

    // SUCCESSFUL INSERTION TESTS

    @Test
    void insertsValidRecord() throws Exception {
        when(repository.save(any(DatasetRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.insert("employees", body("""
                {"name": "Rahul", "age": 28, "department": "Engineering"}
                """));

        verify(repository, times(1)).save(any(DatasetRecord.class));
    }

    @Test
    void acceptsMissingAge() throws Exception {
        when(repository.save(any(DatasetRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.insert("employees", body("""
                {"name": "Meera", "department": "Engineering"}
                """));

        verify(repository).save(any(DatasetRecord.class));
    }

    @Test
    void acceptsAgeZero() throws Exception {
        when(repository.save(any(DatasetRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.insert("employees", body("""
                {"name": "Ananya", "age": 0, "department": "Testing"}
                """));

        verify(repository).save(any(DatasetRecord.class));
    }

    @Test
    void acceptsExtraFields() throws Exception {
        when(repository.save(any(DatasetRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.insert("employees", body("""
                {
                  "name": "Arjun",
                  "age": 28,
                  "department": "Engineering",
                  "salary": 50000
                }
                """));

        ArgumentCaptor<DatasetRecord> captor =
                ArgumentCaptor.forClass(DatasetRecord.class);

        verify(repository).save(captor.capture());

        assertTrue(captor.getValue().getPayload().contains("salary"));
    }

    // QUERY TESTS

    @Test
    void returnsRecordsWithGeneratedIds() {
        when(repository.findByDatasetNameOrderByIdAsc("employees"))
                .thenReturn(List.of(
                        storedRecord(1L,
                                """
                                {"name":"Rahul","age":28,"department":"Engineering"}
                                """)
                ));

        QueryResponse response =
                service.query("employees", null, null, "asc");

        assertNotNull(response.records());
        assertEquals(1, response.records().size());
        assertEquals(1L, response.records().get(0).get("id").asLong());
        assertEquals("Rahul", response.records().get(0).get("name").asText());
    }

    @Test
    void excludesExtraFieldsFromQueryResponse() {
        when(repository.findByDatasetNameOrderByIdAsc("employees"))
                .thenReturn(List.of(
                        storedRecord(1L,
                                """
                                {
                                  "name":"Arjun",
                                  "age":28,
                                  "department":"Engineering",
                                  "city":"Hyderabad",
                                  "salary":50000
                                }
                                """)
                ));

        QueryResponse response =
                service.query("employees", null, null, "asc");

        var result = response.records().get(0);

        assertFalse(result.has("city"));
        assertFalse(result.has("salary"));
        assertEquals(1L, result.get("id").asLong());
    }

    @Test
    void sortsRecordsByAgeDescending() {
        when(repository.findByDatasetNameOrderByIdAsc("employees"))
                .thenReturn(List.of(
                        storedRecord(1L,
                                """
                                {"name":"Rahul","age":25,"department":"Engineering"}
                                """),
                        storedRecord(2L,
                                """
                                {"name":"Priya","age":32,"department":"Testing"}
                                """)
                ));

        QueryResponse response =
                service.query("employees", null, "age", "desc");

        assertNotNull(response.sortedRecords());
        assertEquals(2, response.sortedRecords().size());
        assertEquals(2L,
                response.sortedRecords().get(0).get("id").asLong());
        assertEquals(1L,
                response.sortedRecords().get(1).get("id").asLong());
    }

    @Test
    void groupsRecordsByDepartment() {
        when(repository.findByDatasetNameOrderByIdAsc("employees"))
                .thenReturn(List.of(
                        storedRecord(1L,
                                """
                                {"name":"Rahul","age":28,"department":"Engineering"}
                                """),
                        storedRecord(2L,
                                """
                                {"name":"Priya","age":32,"department":"Testing"}
                                """),
                        storedRecord(3L,
                                """
                                {"name":"Ananya","age":25,"department":"Engineering"}
                                """)
                ));

        QueryResponse response =
                service.query("employees", "department", null, "asc");

        assertNotNull(response.groupedRecords());
        assertEquals(2,
                response.groupedRecords().get("Engineering").size());
        assertEquals(1,
                response.groupedRecords().get("Testing").size());
    }

    @Test
    void throwsExceptionWhenDatasetHasNoRecords() {
        when(repository.findByDatasetNameOrderByIdAsc("ghost"))
                .thenReturn(List.of());

        assertThrows(
                DatasetNotFoundException.class,
                () -> service.query("ghost", null, null, "asc")
        );
    }
}
