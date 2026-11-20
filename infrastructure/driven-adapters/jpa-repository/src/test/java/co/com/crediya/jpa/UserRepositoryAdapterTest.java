package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.EmailAlreadyRegisteredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository repository;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    private User sampleUser;
    private UserEntity sampleEntity;

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

        sampleEntity = new UserEntity();
        sampleEntity.setId(1L);
        sampleEntity.setNombres("Carlos");
        sampleEntity.setApellidos("Perez");
        sampleEntity.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        sampleEntity.setDireccion("Calle 123");
        sampleEntity.setTelefono("3001234567");
        sampleEntity.setCorreoElectronico("carlos.perez@example.com");
        sampleEntity.setSalarioBase(new BigDecimal("3000000"));
    }

    @Test
    void shouldSaveUser() {
        when(repository.save(any(UserEntity.class))).thenReturn(sampleEntity);

        User result = adapter.saveUser(sampleUser);

        assertNotNull(result);
        assertEquals(sampleUser.getId(), result.getId());
        assertEquals(sampleUser.getCorreoElectronico(), result.getCorreoElectronico());
    }

    @Test
    void shouldThrowEmailAlreadyRegisteredExceptionOnDataIntegrityViolation() {
        when(repository.save(any(UserEntity.class))).thenThrow(new DataIntegrityViolationException("Duplicate"));

        assertThrows(EmailAlreadyRegisteredException.class, () -> adapter.saveUser(sampleUser));
    }

    @Test
    void shouldCheckIfExistsByCorreoElectronico() {
        when(repository.existsByCorreoElectronico("carlos.perez@example.com")).thenReturn(true);

        boolean exists = adapter.existsByCorreoElectronico("carlos.perez@example.com");

        assertTrue(exists);
    }
}
