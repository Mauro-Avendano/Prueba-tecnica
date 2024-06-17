package com.example.demo.controller;

import com.example.demo.UserService;
import com.example.demo.database.User;
import com.example.demo.exceptions.EmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testCreateUser() throws Exception {
        String name = "Juan Pérez";
        String email = "juan@gmail.com";
        String password = "passwordValida123";
        String createdTime = "2019-03-27T10:15:30";
        String modifiedTime = "2019-03-27T10:15:30";
        String lastLoginTime = "2019-03-27T10:15:30";
        Boolean isActive = true;
        LocalDateTime created = LocalDateTime.parse(createdTime);
        LocalDateTime modified = LocalDateTime.parse(modifiedTime);
        LocalDateTime lastLogin = LocalDateTime.parse(lastLoginTime);
        PhoneRequest phone1 = new PhoneRequest("12345", "23", "56");
        PhoneRequest phone2 = new PhoneRequest("123456", "22", "55");
        PhoneRequest phone3 = new PhoneRequest("1234567", "21", "54");
        List<PhoneRequest> phones = new ArrayList<>(List.of(phone1, phone2, phone3));
        UserRequest userRequest = new UserRequest(name, email, password, phones);
        User createdUser = new User();
        createdUser.setId(UUID.randomUUID());
        createdUser.setName(name);
        createdUser.setEmail(email);
        createdUser.setCreated(created);
        createdUser.setModified(modified);
        createdUser.setLast_login(lastLogin);
        createdUser.setIsActive(isActive);
        String requestBody = new ObjectMapper().writeValueAsString(userRequest);

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(createdUser.getId().toString()))
                        .andExpect(jsonPath("$.created").value(createdUser.getCreated().toString()))
                        .andExpect(jsonPath("$.modified").value(createdUser.getModified().toString()))
                        .andExpect(jsonPath("$.last_login").value(createdUser.getLast_login().toString()))
                        .andExpect(jsonPath("$.isactive").value(createdUser.getIsActive()));
    }

    @Test
    public void testCreateUserWithInvalidEmail() throws Exception {
        UserRequest userRequest = new UserRequest("Juan Pérez", "invalid-email", "password", null);

        String requestBody = new ObjectMapper().writeValueAsString(userRequest);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().json("{\"mensaje\": \"correo inválido\"}"));
    }

    @Test
    public void testCreateUserWithNoPhones() throws Exception {
        UserRequest userRequest = new UserRequest("Juan Pérez", "juan@gmail.com", "password", null);

        String requestBody = new ObjectMapper().writeValueAsString(userRequest);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().json("{\"mensaje\": \"falta la propiedad phones o su contenido es inválido\"}"));
    }

    @Test
    public void testCreateUserWithNoName() throws Exception {
        PhoneRequest phone1 = new PhoneRequest("12345", "23", "56");
        List<PhoneRequest> phones = new ArrayList<>(List.of(phone1));
        UserRequest userRequest = new UserRequest(null, "juan@gmail.com", "password", phones);

        String requestBody = new ObjectMapper().writeValueAsString(userRequest);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"mensaje\": \"falta la propiedad nombre\"}"));
    }

    @Test
    public void testCreateUserWithNoPassword() throws Exception {
        PhoneRequest phone1 = new PhoneRequest("12345", "23", "56");
        List<PhoneRequest> phones = new ArrayList<>(List.of(phone1));
        UserRequest userRequest = new UserRequest("Juan", "juan@gmail.com", "", phones);

        String requestBody = new ObjectMapper().writeValueAsString(userRequest);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"mensaje\": \"falta la propiedad password\"}"));
    }

    @Test
    public void testCreateUserWithInvalidPassword() throws Exception {
        PhoneRequest phone1 = new PhoneRequest("12345", "23", "56");
        List<PhoneRequest> phones = new ArrayList<>(List.of(phone1));
        UserRequest userRequest = new UserRequest("Juan", "juan@gmail.com", "invalid-password", phones);

        String requestBody = new ObjectMapper().writeValueAsString(userRequest);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"mensaje\": \"por favor introduzca una password con al menos 8 carácteres y un número\"}"));
    }

    @Test
    public void testCreateUserWithAnEmailThatAlreadyExists() throws Exception {
        PhoneRequest phone1 = new PhoneRequest("12345", "23", "56");
        List<PhoneRequest> phones = new ArrayList<>(List.of(phone1));
        UserRequest userRequest = new UserRequest("Juan", "juan@gmail.com", "validpassw0rd", phones);
        String requestBody = new ObjectMapper().writeValueAsString(userRequest);

        when(userService.createUser(any(User.class))).thenThrow(new EmailAlreadyExistsException("el email ya existe"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{\"mensaje\": \"el correo ya existe\"}"));
    }

    @Test
    public void testCreateUserInternalServerError() throws Exception {
        PhoneRequest phone1 = new PhoneRequest("12345", "23", "56");
        List<PhoneRequest> phones = new ArrayList<>(List.of(phone1));
        UserRequest userRequest = new UserRequest("Juan", "juan@gmail.com", "validpassw0rd", phones);
        String requestBody = new ObjectMapper().writeValueAsString(userRequest);
        String exception = "cualquier excepción";

        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException(exception));

        String json = String.format("{\"mensaje\": \"%s\"}", exception);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is5xxServerError())
                .andExpect(content().json(json));
    }
}
