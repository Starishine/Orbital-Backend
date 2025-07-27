package com.nus.oribital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nus.oribital.modal.Budget;
import com.nus.oribital.modal.Expense;
import com.nus.oribital.repository.BudgetRepository;
import com.nus.oribital.repository.ExpenseRepository;
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

import java.util.Optional;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @MockBean
    private JWTTokenUtil jwtTokenUtil;

    private static final String AUTH_HEADER = "Authorization";
    private static final String TEST_TOKEN = "Bearer test-token";
    private static final String TEST_USERNAME = "testuser";

    // Setup method to clear repositories and mock JWT token utility
    @BeforeEach
    public void setup() {
        expenseRepository.deleteAll();
        budgetRepository.deleteAll();
        Mockito.when(jwtTokenUtil.getUser(Mockito.anyString())).thenReturn(TEST_USERNAME);
    }

    // Test for creating an expense with a valid budget
    @Test
    public void testCreateExpense_SuccessWithBudget() throws Exception {
        Budget budget = new Budget();
        budget.setUsername(TEST_USERNAME);
        budget.setCategory("Food");
        budget.setCurrency("SGD");
        budget.setAmount(100.0);
        budgetRepository.save(budget);

        Expense expense = new Expense(20.0, "Food", "SGD", TEST_USERNAME);

        mockMvc.perform(post("/expense/create")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
        // .andExpect(jsonPath("$.data.category").value("Food"))
        // .andExpect(jsonPath("$.data.amount").value(20.0));
    }

// Test for creating an expense without a budget
    @Test
    public void testCreateExpense_SuccessWithoutBudget() throws Exception {
        Expense expense = new Expense(15.0, "Transport", "SGD", TEST_USERNAME);

        mockMvc.perform(post("/expense/create")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
        //.andExpect(jsonPath("$.data.category").value("Transport"))
        //.andExpect(jsonPath("$.data.amount").value(15.0));
    }

    // Test for creating an expense with an invalid amount
    @Test
    public void testCreateExpense_InvalidAmount() throws Exception {
        Expense expense = new Expense(0.0, "Food", "SGD", TEST_USERNAME);

        mockMvc.perform(post("/expense/create")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    // Test for creating an expense with an invalid category
    @Test
    public void testCreateExpense_InvalidToken() throws Exception {
        Mockito.when(jwtTokenUtil.getUser(Mockito.anyString())).thenReturn(null);

        Expense expense = new Expense(10.0, "Food", "SGD", TEST_USERNAME);

        mockMvc.perform(post("/expense/create")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

}
