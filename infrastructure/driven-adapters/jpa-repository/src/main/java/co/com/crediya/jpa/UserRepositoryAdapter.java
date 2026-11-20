package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository repository;

    @Override
    @Transactional
    public User saveUser(User user) {
        log.debug("Guardando usuario en base de datos");
        try {
            return toDomain(repository.save(toEntity(user)));
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyRegisteredException(user.getCorreoElectronico());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCorreoElectronico(String correoElectronico) {
        return repository.existsByCorreoElectronico(correoElectronico);
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setNombres(user.getNombres());
        entity.setApellidos(user.getApellidos());
        entity.setFechaNacimiento(user.getFechaNacimiento());
        entity.setDireccion(user.getDireccion());
        entity.setTelefono(user.getTelefono());
        entity.setCorreoElectronico(user.getCorreoElectronico());
        entity.setSalarioBase(user.getSalarioBase());
        return entity;
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .fechaNacimiento(entity.getFechaNacimiento())
                .direccion(entity.getDireccion())
                .telefono(entity.getTelefono())
                .correoElectronico(entity.getCorreoElectronico())
                .salarioBase(entity.getSalarioBase())
                .build();
    }
}
