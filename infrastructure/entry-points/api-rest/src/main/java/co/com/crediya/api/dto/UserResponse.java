package co.com.crediya.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserResponse(
        Long id,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String direccion,
        String telefono,
        String correoElectronico,
        BigDecimal salarioBase
) {
}
