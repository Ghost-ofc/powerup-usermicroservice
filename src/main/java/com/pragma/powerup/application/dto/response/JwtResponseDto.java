package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta de autenticación exitosa")
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private String correo;
    private String rol;

    public JwtResponseDto(String token, String correo, String rol) {
        this.token = token;
        this.correo = correo;
        this.rol = rol;
        this.type = "Bearer";
    }
}
