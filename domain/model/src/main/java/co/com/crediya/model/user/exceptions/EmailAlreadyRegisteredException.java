package co.com.crediya.model.user.exceptions;

public class EmailAlreadyRegisteredException extends BusinessException {

    public EmailAlreadyRegisteredException(String email) {
        super("El correo electrónico " + email + " ya se encuentra registrado");
    }
}
