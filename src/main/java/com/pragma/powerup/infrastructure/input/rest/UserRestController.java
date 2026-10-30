package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios del sistema")
public class UserRestController {

    private final IUserHandler userHandler;

    @Operation(summary = "Crear nuevo propietario de restaurante",
            description = "Permite a un Administrador registrar una cuenta para un propietario de restaurante.",
            security = {@SecurityRequirement(name = "bearer-jwt")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Propietario creado exitosamente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (menor de edad, formato de teléfono o documento no numérico)", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado o token JWT inválido/expirado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: solo el Administrador puede crear propietarios", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto: ya existe un usuario con ese correo o documento de identidad", content = @Content)
    })
    @PostMapping("/owner")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN')")
    public ResponseEntity<Void> saveOwner(@Valid @RequestBody UserRequestDto userRequestDto) {
        userHandler.saveOwner(userRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Consultar usuario por correo",
            description = "Consulta la información básica de un usuario por su correo electrónico.",
            security = {@SecurityRequirement(name = "bearer-jwt")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/email/{correo}")
    public ResponseEntity<UserResponseDto> getByCorreo(@PathVariable String correo) {
        return ResponseEntity.ok(userHandler.getByCorreo(correo));
    }

    @Operation(summary = "Consultar usuario por ID",
            description = "Consulta la información básica de un usuario por su ID para validaciones entre microservicios.",
            security = {@SecurityRequirement(name = "bearer-jwt")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userHandler.getById(id));
    }
}
