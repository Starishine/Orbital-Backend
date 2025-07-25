package com.controller;

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
    private JWTTokenUtil jwtTokenUtil;

    private static final String AUTH_HEADER = "Authorization";
    private static final String TEST_TOKEN = "Bearer test-token";
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    public void setup() {
        // Clean up test data before each test
        budgetRepository.deleteAll();
        // Mock JWTTokenUtil to always return TEST_USERNAME
        Mockito.when(jwtTokenUtil.getUser(Mockito.anyString())).thenReturn(TEST_USERNAME);
    }

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
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.category").value("Food"))
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME));
    }

    @Test
    public void testGetBudgets_Unauthorized() throws Exception {
        // No auth header, should fail with 400 (missing header)
        mockMvc.perform(get("/budget/get"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateBudget_Success() throws Exception {
        // Insert a budget first
        Budget budget = new Budget();
        budget.setCategory("Food");
        budget.setCurrency("SGD");
        budget.setAmountAllocated(100);
        budget.setAmount(100);
        budget.setUsername(TEST_USERNAME);
        budget = budgetRepository.save(budget);

        // Update the budget
        budget.setAmountAllocated(120);
        budget.setAmount(110);

        mockMvc.perform(put("/budget/update")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budget)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.amountAllocated").value(120.0))
                .andExpect(jsonPath("$.data.amount").value(110.0));
    }

    @Test
    public void testDeleteBudget_Success() throws Exception {
        // Insert a budget first
        Budget budget = new Budget();
        budget.setCategory("Food");
        budget.setCurrency("SGD");
        budget.setAmountAllocated(100);
        budget.setAmount(100);
        budget.setUsername(TEST_USERNAME);
        budget = budgetRepository.save(budget);

        mockMvc.perform(delete("/budget/delete/" + budget.getId())
                .header(AUTH_HEADER, TEST_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
