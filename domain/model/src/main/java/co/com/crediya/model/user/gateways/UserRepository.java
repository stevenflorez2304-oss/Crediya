package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;

public interface UserRepository {

    User saveUser(User user);

    boolean existsByCorreoElectronico(String correoElectronico);
}
