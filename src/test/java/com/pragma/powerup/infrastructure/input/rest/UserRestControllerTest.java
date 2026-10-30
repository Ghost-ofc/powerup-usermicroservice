package com.pragma.powerup.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.domain.exception.UnderageUserException;
import com.pragma.powerup.domain.exception.UserAlreadyExistsException;
import com.pragma.powerup.infrastructure.exceptionhandler.ControllerAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private IUserHandler userHandler;

    @InjectMocks
    private UserRestController userRestController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(userRestController)
                .setControllerAdvice(new ControllerAdvisor())
                .build();
    }

    @Test
    @DisplayName("Debe responder 201 Created al registrar un propietario válido")
    void saveOwner_whenValidPayload_shouldReturn201() throws Exception {
        UserRequestDto requestDto = new UserRequestDto(
                "Carlos",
                "Gómez",
                "1098765432",
                "+573005698325",
                LocalDate.of(1990, 8, 20),
                "carlos.gomez@restaurante.com",
                "ClaveSegura123!"
        );

        doNothing().when(userHandler).saveOwner(any(UserRequestDto.class));

        mockMvc.perform(post("/api/v1/user/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Debe responder 400 Bad Request cuando el usuario es menor de edad")
    void saveOwner_whenUnderage_shouldReturn400() throws Exception {
        UserRequestDto requestDto = new UserRequestDto(
                "Carlos",
                "Gómez",
                "1098765432",
                "+573005698325",
                LocalDate.now().minusYears(16),
                "carlos.gomez@restaurante.com",
                "ClaveSegura123!"
        );

        doThrow(new UnderageUserException()).when(userHandler).saveOwner(any(UserRequestDto.class));

        mockMvc.perform(post("/api/v1/user/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The user must be of legal age (18 years or older)."));
    }

    @Test
    @DisplayName("Debe responder 409 Conflict cuando el usuario ya existe")
    void saveOwner_whenUserAlreadyExists_shouldReturn409() throws Exception {
        UserRequestDto requestDto = new UserRequestDto(
                "Carlos",
                "Gómez",
                "1098765432",
                "+573005698325",
                LocalDate.of(1990, 8, 20),
                "carlos.gomez@restaurante.com",
                "ClaveSegura123!"
        );

        doThrow(new UserAlreadyExistsException("A user with this email already exists."))
                .when(userHandler).saveOwner(any(UserRequestDto.class));

        mockMvc.perform(post("/api/v1/user/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A user with this email already exists."));
    }

    @Test
    @DisplayName("Debe responder 400 Bad Request cuando el DTO tiene campos inválidos por Bean Validation")
    void saveOwner_whenInvalidDtoFields_shouldReturn400() throws Exception {
        UserRequestDto invalidDto = new UserRequestDto(
                "",
                "",
                "abc-not-numeric",
                "+573005698325000000000",
                null,
                "invalid-email",
                ""
        );

        mockMvc.perform(post("/api/v1/user/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
