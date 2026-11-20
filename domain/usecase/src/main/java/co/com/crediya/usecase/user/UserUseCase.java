package co.com.crediya.usecase.user;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.BusinessException;
import co.com.crediya.model.user.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class UserUseCase {

    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");

    private final UserRepository userRepository;

    public User saveUser(User user) {
        validateRequiredFields(user);
        validateSalary(user);

        if (userRepository.existsByCorreoElectronico(user.getCorreoElectronico())) {
            throw new EmailAlreadyRegisteredException(user.getCorreoElectronico());
        }

        return userRepository.saveUser(user);
    }

    private void validateRequiredFields(User user) {
        if (isBlank(user.getNombres()) || isBlank(user.getApellidos())
                || isBlank(user.getCorreoElectronico()) || user.getSalarioBase() == null) {
            throw new BusinessException(
                    "nombres, apellidos, correo_electronico y salario_base son obligatorios");
        }
    }

    private void validateSalary(User user) {
        if (user.getSalarioBase().compareTo(MIN_SALARY) < 0
                || user.getSalarioBase().compareTo(MAX_SALARY) > 0) {
            throw new BusinessException("El salario_base debe estar entre 0 y 15000000");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
