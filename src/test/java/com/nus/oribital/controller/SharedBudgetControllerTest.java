package com.nus.oribital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nus.oribital.modal.Invitee;
import com.nus.oribital.modal.SharedBudget;
import com.nus.oribital.repository.SharedBudgetRepository;
import com.nus.oribital.util.EmailUtil;
import com.nus.oribital.util.JWTTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class SharedBudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedBudgetRepository sharedBudgetRepository;

    @MockBean
    private JWTTokenUtil jwtTokenUtil;

    private static final String AUTH_HEADER = "Authorization";
    private static final String TEST_TOKEN = "Bearer test-token";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "testuser@email.com";

    @BeforeEach
    public void setup() {
        sharedBudgetRepository.deleteAll();
        Mockito.when(jwtTokenUtil.getUser(Mockito.anyString())).thenReturn(TEST_USERNAME);
    }

    // @Test
    // public void testCreateSharedBudget_Success() throws Exception {
    //     Invitee invitee = new Invitee();
    //     invitee.setUsername("invitee1");
    //     invitee.setEmail("invitee1@email.com");

    //     SharedBudget sharedBudget = new SharedBudget(
    //         "Group Food",           // category
    //         200.0,                  // amount
    //         0.0,                    // spent
    //         "SGD",                  // currency
    //         Collections.singletonList(invitee), // invites
    //         TEST_USERNAME,          // inviter
    //         TEST_EMAIL              // inviterEmail
    //     );

    //     // Mock static EmailUtil.sendMail and EmailUtil.renderInviteEmail
    //     try (MockedStatic<EmailUtil> emailUtilMock = Mockito.mockStatic(EmailUtil.class)) {
    //         emailUtilMock.doNothing().when(() -> EmailUtil.sendMail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()));
    //         emailUtilMock.when(() -> EmailUtil.renderInviteEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
    //                 .thenReturn("Rendered Email");

    //         mockMvc.perform(post("/shared-budget/create")
    //                 .header(AUTH_HEADER, TEST_TOKEN)
    //                 .contentType(MediaType.APPLICATION_JSON)
    //                 .content(objectMapper.writeValueAsString(sharedBudget)))
    //                 .andDo(print())
    //                 .andExpect(status().isOk())
    //                 .andExpect(jsonPath("$.status").value("SUCCESS"))
    //                 .andExpect(jsonPath("$.data.category").value("Group Food"))
    //                 .andExpect(jsonPath("$.data.inviter").value(TEST_USERNAME));
    //     }
    // }

    @Test
    public void testUpdateSharedBudget_Success() throws Exception {
        SharedBudget sharedBudget = new SharedBudget(
            "Group Food", 200.0, 0.0, "SGD", new ArrayList<>(), TEST_USERNAME, TEST_EMAIL
        );
        sharedBudget = sharedBudgetRepository.save(sharedBudget);

        sharedBudget.setAmount(300.0);
        sharedBudget.setCategory("Updated Category");

        mockMvc.perform(put("/shared-budget/update")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sharedBudget)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.amount").value(300.0))
                .andExpect(jsonPath("$.data.category").value("Updated Category"));
    }

    @Test
    public void testUpdateSharedBudget_NotFound() throws Exception {
        SharedBudget sharedBudget = new SharedBudget(
            "Group Food", 200.0, 0.0, "SGD", new ArrayList<>(), TEST_USERNAME, TEST_EMAIL
        );
        sharedBudget.setId("nonexistent-id");

        mockMvc.perform(put("/shared-budget/update")
                .header(AUTH_HEADER, TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sharedBudget)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testGetSharedBudgets_Success() throws Exception {
        SharedBudget sharedBudget = new SharedBudget(
            "Group Food", 200.0, 0.0, "SGD", new ArrayList<>(), TEST_USERNAME, TEST_EMAIL
        );
        sharedBudgetRepository.save(sharedBudget);

        mockMvc.perform(get("/shared-budget/get")
                .header(AUTH_HEADER, TEST_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category").value("Group Food"));
    }

}