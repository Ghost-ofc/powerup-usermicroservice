package com.pragma.powerup.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Credenciales de inicio de sesión")
public class LoginRequestDto {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    @Schema(description = "Correo registrado", example = "admin@pragma.com")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña de acceso", example = "admin123")
    private String clave;
}
