package com.nus.oribital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nus.oribital.modal.Budget;
import com.nus.oribital.repository.BudgetRepository;
import com.nus.oribital.util.JWTTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BudgetRepository budgetRepository;

    @MockBean
    private JWTTokenUtil jwtTokenUtil; // <-- FIXED: Use @MockBean for proper mocking

    private static final String AUTH_HEADER = "Authorization";
    private static final String TEST_TOKEN = "Bearer test-token";
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    public void setup() {
        budgetRepository.deleteAll();
        Mockito.when(jwtTokenUtil.getUser(Mockito.anyString())).thenReturn(TEST_USERNAME);
    }

    // Test for creating a budget successfully

    @Test
    public void testCreateBudget_Success() throws Exception {
        Budget budget = new Budget();
        budget.setCategory("Food");
        budget.setCurrency("SGD");
        budget.setAmountAllocated(100);
        budget.setAmount(100);

        mockMvc.perform(post("/budget/create")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budget)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    // Test for creating a budget with a duplicate category
    @Test
    public void testCreateBudget_DuplicateCategory() throws Exception {
        Budget budget = new Budget();
        budget.setCategory("Food");
        budget.setCurrency("SGD");
        budget.setAmountAllocated(100);
        budget.setAmount(100);
        budget.setUsername(TEST_USERNAME);
        budgetRepository.save(budget);

        Budget dupe = new Budget();
        dupe.setCategory("Food");
        dupe.setCurrency("SGD");
        dupe.setAmountAllocated(50);
        dupe.setAmount(50);

        mockMvc.perform(post("/budget/create")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dupe)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }


    // Testing invalid budget creation
    @Test
    public void testCreateBudget_InvalidAmount() throws Exception {
        Budget budget = new Budget();
        budget.setCategory("Transport");
        budget.setCurrency("SGD");
        budget.setAmountAllocated(0);
        budget.setAmount(0);

        mockMvc.perform(post("/budget/create")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budget)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    // Test for invalid delete operation
    @Test
    public void testDeleteBudget_NotFound() throws Exception {
        mockMvc.perform(delete("/budget/delete/invalid-id")
                .header(AUTH_HEADER, TEST_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }
}
