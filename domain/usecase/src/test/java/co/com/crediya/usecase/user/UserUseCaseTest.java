package co.com.crediya.usecase.user;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.BusinessException;
import co.com.crediya.model.user.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .nombres("Carlos")
                .apellidos("Perez")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .direccion("Calle 123")
                .telefono("3001234567")
                .correoElectronico("carlos.perez@example.com")
                .salarioBase(new BigDecimal("3000000"))
                .build();
    }

    @Test
    void shouldSaveUserSuccessfully() {
        when(userRepository.existsByCorreoElectronico("carlos.perez@example.com")).thenReturn(false);
        when(userRepository.saveUser(any(User.class))).thenReturn(sampleUser);

        User savedUser = userUseCase.saveUser(sampleUser);

        assertNotNull(savedUser);
        assertEquals("carlos.perez@example.com", savedUser.getCorreoElectronico());
        verify(userRepository).saveUser(sampleUser);
    }

    @Test
    void shouldThrowExceptionWhenRequiredFieldIsBlank() {
        User invalidUser = sampleUser.toBuilder().nombres("   ").build();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userUseCase.saveUser(invalidUser)
        );
        assertEquals("nombres, apellidos, correo_electronico y salario_base son obligatorios", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSalaryIsNull() {
        User invalidUser = sampleUser.toBuilder().salarioBase(null).build();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userUseCase.saveUser(invalidUser)
        );
        assertEquals("nombres, apellidos, correo_electronico y salario_base son obligatorios", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSalaryIsNegative() {
        User invalidUser = sampleUser.toBuilder().salarioBase(new BigDecimal("-1")).build();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userUseCase.saveUser(invalidUser)
        );
        assertEquals("El salario_base debe estar entre 0 y 15000000", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSalaryExceedsMax() {
        User invalidUser = sampleUser.toBuilder().salarioBase(new BigDecimal("15000001")).build();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userUseCase.saveUser(invalidUser)
        );
        assertEquals("El salario_base debe estar entre 0 y 15000000", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByCorreoElectronico("carlos.perez@example.com")).thenReturn(true);

        EmailAlreadyRegisteredException exception = assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> userUseCase.saveUser(sampleUser)
        );
        assertEquals("El correo electrónico carlos.perez@example.com ya se encuentra registrado", exception.getMessage());
    }
}
