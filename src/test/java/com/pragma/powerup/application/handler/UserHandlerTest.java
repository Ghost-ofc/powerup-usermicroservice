package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.handler.impl.UserHandler;
import com.pragma.powerup.application.mapper.IUserRequestMapper;
import com.pragma.powerup.application.mapper.IUserResponseMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.domain.model.UserModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    @Mock
    private IUserServicePort userServicePort;

    @Mock
    private IUserRequestMapper userRequestMapper;

    @Mock
    private IUserResponseMapper userResponseMapper;

    @InjectMocks
    private UserHandler userHandler;

    @Test
    @DisplayName("Debe mapear DTO y llamar a saveOwner en el servicio")
    void saveOwner_shouldMapAndCallDomainService() {
        UserRequestDto requestDto = new UserRequestDto(
                "Carlos", "Gómez", "12345", "+573001234567",
                LocalDate.of(1995, 5, 20), "carlos@correo.com", "clave123"
        );
        UserModel userModel = new UserModel();

        when(userRequestMapper.toUser(requestDto)).thenReturn(userModel);

        userHandler.saveOwner(requestDto);

        verify(userRequestMapper).toUser(requestDto);
        verify(userServicePort).saveOwner(userModel);
    }

    @Test
    @DisplayName("Debe consultar por correo y mapear a response DTO")
    void getByCorreo_shouldFindAndMapToResponseDto() {
        UserModel userModel = new UserModel(
                1L, "Carlos", "Gómez", "12345", "+573001234567",
                LocalDate.of(1995, 5, 20), "carlos@correo.com", "hashed",
                new RoleModel(2L, "ROLE_PROPIETARIO", "Propietario")
        );
        UserResponseDto responseDto = new UserResponseDto(
                1L, "Carlos", "Gómez", "12345", "+573001234567",
                LocalDate.of(1995, 5, 20), "carlos@correo.com", "ROLE_PROPIETARIO"
        );

        when(userServicePort.findByCorreo("carlos@correo.com")).thenReturn(userModel);
        when(userResponseMapper.toResponse(userModel)).thenReturn(responseDto);

        UserResponseDto result = userHandler.getByCorreo("carlos@correo.com");

        assertNotNull(result);
        assertEquals("carlos@correo.com", result.getCorreo());
        assertEquals("ROLE_PROPIETARIO", result.getRolNombre());
        verify(userServicePort).findByCorreo("carlos@correo.com");
        verify(userResponseMapper).toResponse(userModel);
    }

    @Test
    @DisplayName("Debe consultar por id y mapear a response DTO")
    void getById_shouldFindAndMapToResponseDto() {
        UserModel userModel = new UserModel(
                1L, "Carlos", "Gómez", "12345", "+573001234567",
                LocalDate.of(1995, 5, 20), "carlos@correo.com", "hashed",
                new RoleModel(2L, "ROLE_PROPIETARIO", "Propietario")
        );
        UserResponseDto responseDto = new UserResponseDto(
                1L, "Carlos", "Gómez", "12345", "+573001234567",
                LocalDate.of(1995, 5, 20), "carlos@correo.com", "ROLE_PROPIETARIO"
        );

        when(userServicePort.findById(1L)).thenReturn(userModel);
        when(userResponseMapper.toResponse(userModel)).thenReturn(responseDto);

        UserResponseDto result = userHandler.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userServicePort).findById(1L);
        verify(userResponseMapper).toResponse(userModel);
    }
}
