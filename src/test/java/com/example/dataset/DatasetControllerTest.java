package com.example.dataset;

import com.example.dataset.controller.DatasetController;
import com.example.dataset.dto.InsertRecordResponse;
import com.example.dataset.exception.DatasetNotFoundException;
import com.example.dataset.exception.GlobalExceptionHandler;
import com.example.dataset.service.DatasetService;
import com.fasterxml.jackson.databind.node.IntNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DatasetController.class)
@Import(GlobalExceptionHandler.class)
class DatasetControllerTest {

    @Autowired MockMvc mvc;
    @MockBean DatasetService service;

    @Test
    void insertReturns201() throws Exception {
        when(service.insert(eq("employee_dataset"), any()))
                .thenReturn(new InsertRecordResponse("Record added successfully", "employee_dataset", IntNode.valueOf(1)));

        mvc.perform(post("/api/dataset/employee_dataset/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"John Doe\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Record added successfully"))
                .andExpect(jsonPath("$.recordId").value(1));
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        mvc.perform(post("/api/dataset/employee_dataset/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidDatasetNameReturns400() throws Exception {
        mvc.perform(post("/api/dataset/bad$name/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownDatasetReturns404() throws Exception {
        when(service.query(eq("ghost"), any(), any(), any())).thenThrow(new DatasetNotFoundException("ghost"));

        mvc.perform(get("/api/dataset/ghost/query"))
                .andExpect(status().isNotFound());
    }
}
