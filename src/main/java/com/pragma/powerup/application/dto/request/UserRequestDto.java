package com.pragma.powerup.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos para el registro de un nuevo propietario")
public class UserRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del propietario", example = "Carlos")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Schema(description = "Apellido del propietario", example = "Gómez")
    private String apellido;

    @NotBlank(message = "El documento de identidad es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El documento de identidad debe ser únicamente numérico")
    @Schema(description = "Documento de identidad (solo números)", example = "1098765432")
    private String documentoDeIdentidad;

    @NotBlank(message = "El celular es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{1,13}$", message = "El celular debe contener máximo 13 caracteres y puede iniciar con +")
    @Schema(description = "Número de celular (máx 13 caracteres, puede incluir '+')", example = "+573005698325")
    private String celular;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Schema(description = "Fecha de nacimiento (debe ser mayor de 18 años)", example = "1990-08-20")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato de correo no es válido")
    @Schema(description = "Correo electrónico válido y único", example = "carlos.gomez@restaurante.com")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña de acceso (será encriptada)", example = "MiClaveSegura123")
    private String clave;
}
