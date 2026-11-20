package co.com.crediya.api;

import co.com.crediya.api.exception.GlobalExceptionHandler;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.BusinessException;
import co.com.crediya.model.user.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserUseCase userUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);

        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .setMessageConverters(converter)
                .build();
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        User savedUser = User.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Perez")
                .fechaNacimiento(LocalDate.of(1995, 1, 1))
                .correoElectronico("juan.perez@example.com")
                .salarioBase(new BigDecimal("2500000"))
                .build();

        when(userUseCase.saveUser(any(User.class))).thenReturn(savedUser);

        String json = """
                {
                    "nombres": "Juan",
                    "apellidos": "Perez",
                    "fecha_nacimiento": "1995-01-01",
                    "correo_electronico": "juan.perez@example.com",
                    "salario_base": 2500000
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombres").value("Juan"))
                .andExpect(jsonPath("$.apellidos").value("Perez"));
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        String json = """
                {
                    "nombres": "",
                    "apellidos": "Perez",
                    "correo_electronico": "invalid-email",
                    "salario_base": -100
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyRegistered() throws Exception {
        when(userUseCase.saveUser(any(User.class)))
                .thenThrow(new EmailAlreadyRegisteredException("juan.perez@example.com"));

        String json = """
                {
                    "nombres": "Juan",
                    "apellidos": "Perez",
                    "correo_electronico": "juan.perez@example.com",
                    "salario_base": 2500000
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestWhenBusinessExceptionOccurs() throws Exception {
        when(userUseCase.saveUser(any(User.class)))
                .thenThrow(new BusinessException("Error de negocio"));

        String json = """
                {
                    "nombres": "Juan",
                    "apellidos": "Perez",
                    "correo_electronico": "juan.perez@example.com",
                    "salario_base": 2500000
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
