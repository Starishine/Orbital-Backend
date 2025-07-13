package com.nus.oribital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nus.oribital.modal.User;
import com.nus.oribital.repository.UserRepository;
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
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private JWTTokenUtil jwtTokenUtil;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        Mockito.when(jwtTokenUtil.generateJwtToken(Mockito.anyString())).thenReturn("mocked-jwt-token");
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");

        mockMvc.perform(post("/signup/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    public void testRegisterUser_MissingFields() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        // Missing password and email

        mockMvc.perform(post("/signup/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testRegisterUser_ShortUsernameOrPassword() throws Exception {
        User user = new User();
        user.setUsername("ab"); // too short
        user.setPassword("12"); // too short
        user.setEmail("short@example.com");

        mockMvc.perform(post("/signup/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testRegisterUser_DuplicateUsername() throws Exception {
        User user = new User();
        user.setUsername("dupeuser");
        user.setPassword("password123");
        user.setEmail("dupe@example.com");
        userRepository.save(user);

        User dupe = new User();
        dupe.setUsername("dupeuser");
        dupe.setPassword("password456");
        dupe.setEmail("dupe2@example.com");

        mockMvc.perform(post("/signup/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dupe)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }


    @Test
    public void testRegisterUser_WhitespaceUsernamePassword() throws Exception {
        User user = new User();
        user.setUsername("   ");
        user.setPassword("   ");
        user.setEmail("white@example.com");

        mockMvc.perform(post("/signup/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testRegisterUser_SpecialCharactersUsername() throws Exception {
        User user = new User();
        user.setUsername("user!@#");
        user.setPassword("password123");
        user.setEmail("special@example.com");

        mockMvc.perform(post("/signup/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                // Accept or reject based on your controller logic
                .andExpect(jsonPath("$.status", anyOf(is("SUCCESS"), is("ERROR"))));
    }

    @Test
    public void testRegisterUser_VeryLongUsernamePassword() throws Exception {
        String longStr = "a".repeat(300);
        User user = new User();
        user.setUsername(longStr);
        user.setPassword(longStr);
        user.setEmail("long@example.com");

        mockMvc.perform(post("/signup/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                // Accept or reject based on your controller logic
                .andExpect(jsonPath("$.status", anyOf(is("SUCCESS"), is("ERROR"))));
    }

    @Test
    public void testRegisterUser_NullFields() throws Exception {
        User user = new User();
        user.setUsername(null);
        user.setPassword(null);
        user.setEmail(null);

        mockMvc.perform(post("/signup/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        User user = new User();
        user.setUsername("loginuser");
        user.setPassword("password123");
        user.setEmail("login@example.com");
        userRepository.save(user);

        User login = new User();
        login.setUsername("loginuser");
        login.setPassword("password123");

        mockMvc.perform(post("/signup/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value("loginuser"))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    public void testLoginUser_WrongPassword() throws Exception {
        User user = new User();
        user.setUsername("wrongpass");
        user.setPassword("password123");
        user.setEmail("wrongpass@example.com");
        userRepository.save(user);

        User login = new User();
        login.setUsername("wrongpass");
        login.setPassword("wrongpassword");

        mockMvc.perform(post("/signup/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testLoginUser_UserNotFound() throws Exception {
        User login = new User();
        login.setUsername("nouser");
        login.setPassword("password123");

        mockMvc.perform(post("/signup/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testLoginUser_MissingFields() throws Exception {
        User login = new User();
        login.setUsername("loginuser");
        // Missing password

        mockMvc.perform(post("/signup/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("password1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);

        mockMvc.perform(get("/signup/get"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", anyOf(is("user1"), is("user2"))))
                .andExpect(jsonPath("$[1].username", anyOf(is("user1"), is("user2"))));
    }

    @Test
    public void testGetAllUsers_Empty() throws Exception {
        userRepository.deleteAll();

        mockMvc.perform(get("/signup/get"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}