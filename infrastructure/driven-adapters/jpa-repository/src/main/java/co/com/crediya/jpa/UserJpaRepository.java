package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByCorreoElectronico(String correoElectronico);
}
