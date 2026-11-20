package co.com.crediya.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateUserRequest(
        @NotBlank(message = "nombres es obligatorio")
        String nombres,

        @NotBlank(message = "apellidos es obligatorio")
        String apellidos,

        LocalDate fechaNacimiento,
        String direccion,
        String telefono,

        @NotBlank(message = "correo_electronico es obligatorio")
        @Email(message = "correo_electronico no tiene un formato válido")
        String correoElectronico,

        @NotNull(message = "salario_base es obligatorio")
        @DecimalMin(value = "0", message = "salario_base debe ser mayor o igual a 0")
        @DecimalMax(value = "15000000", message = "salario_base no puede superar 15000000")
        BigDecimal salarioBase
) {
}
