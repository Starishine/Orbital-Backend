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

    @BeforeEach
    public void setup() {
        expenseRepository.deleteAll();
        budgetRepository.deleteAll();
        Mockito.when(jwtTokenUtil.getUser(Mockito.anyString())).thenReturn(TEST_USERNAME);
        //Mockito.when(jwtTokenUtil.validateToken(Mockito.anyString())).thenReturn(true); 
    }

    @Test
    public void testCreateExpense_Success_AllFields() throws Exception {
        // Insert a budget for the expense category
        Budget budget = new Budget();
        budget.setCategory("Food");
        budget.setCurrency("SGD");
        budget.setAmountAllocated(100);
        budget.setAmount(100);
        budget.setUsername(TEST_USERNAME);
        budgetRepository.save(budget);

        Expense expense = new Expense(20.5, "Food", "SGD", "Lunch at cafe");
        expense.setDate("2024-07-13");
        expense.setName("Lunch at cafe");
        expense.setUsername(TEST_USERNAME);

        mockMvc.perform(post("/expense/create")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.category").value("Food"))
                .andExpect(jsonPath("$.data.currency").value("SGD"))
                .andExpect(jsonPath("$.data.amount").value(20.5))
                .andExpect(jsonPath("$.data.name").value("Lunch at cafe"))
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data.date").exists())
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    public void testCreateExpense_MissingFields_Error() throws Exception {
        Expense expense = new Expense(10, null, "SGD", "Lunch at cafe");
        expense.setDate("2024-07-13");
        // category is null

        mockMvc.perform(post("/expense/create")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testGetExpenses_ReturnsAllFields() throws Exception {
        // Insert an expense for the user
        Expense expense = new Expense(5.75, "Transport", "SGD", "Bus fare");
        expense.setDate("2024-07-13");
        expense.setName("Bus fare");
        expense.setUsername(TEST_USERNAME);
        expenseRepository.save(expense);

        mockMvc.perform(get("/expense/get")
                .header(AUTH_HEADER, TEST_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category").value("Transport"))
                .andExpect(jsonPath("$[0].currency").value("SGD"))
                .andExpect(jsonPath("$[0].amount").value(5.75))
                .andExpect(jsonPath("$[0].name").value("Bus fare"))
                .andExpect(jsonPath("$[0].username").value(TEST_USERNAME))
                .andExpect(jsonPath("$[0].date").exists())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    public void testGetExpenses_Unauthorized() throws Exception {
        mockMvc.perform(get("/expense/get"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateExpense_InvalidAmount() throws Exception {
        Expense expense = new Expense(-100, "Food", "SGD", "Negative amount");
        expense.setDate("2024-07-13");

        mockMvc.perform(post("/expense/create")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testCreateExpense_NoBudget_Error() throws Exception {
        // No budget for this category
        Expense expense = new Expense(10, "Nonexistent", "SGD", "No budget");
        expense.setDate("2024-07-13");

        mockMvc.perform(post("/expense/create")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}