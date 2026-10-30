package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.exception.InvalidDocumentException;
import com.pragma.powerup.domain.exception.InvalidEmailException;
import com.pragma.powerup.domain.exception.InvalidPhoneException;
import com.pragma.powerup.domain.exception.RequiredFieldException;
import com.pragma.powerup.domain.exception.RoleNotFoundException;
import com.pragma.powerup.domain.exception.UnderageUserException;
import com.pragma.powerup.domain.exception.UserAlreadyExistsException;
import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IRolePersistencePort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class UserUseCase implements IUserServicePort {

    public static final String ROLE_PROPIETARIO = "ROLE_PROPIETARIO";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^\\+?[0-9]{1,13}$";
    private static final String NUMERIC_DOCUMENT_REGEX = "^[0-9]+$";

    private final IUserPersistencePort userPersistencePort;
    private final IRolePersistencePort rolePersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;

    public UserUseCase(IUserPersistencePort userPersistencePort,
                       IRolePersistencePort rolePersistencePort,
                       IPasswordEncoderPort passwordEncoderPort) {
        this.userPersistencePort = userPersistencePort;
        this.rolePersistencePort = rolePersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public void saveOwner(UserModel userModel) {
        validateRequiredFields(userModel);
        validateDocumentFormat(userModel.getDocumentoDeIdentidad());
        validatePhoneFormat(userModel.getCelular());
        validateEmailFormat(userModel.getCorreo());
        validateAge(userModel.getFechaNacimiento());
        validateUniqueness(userModel.getCorreo(), userModel.getDocumentoDeIdentidad());

        RoleModel role = rolePersistencePort.findByName(ROLE_PROPIETARIO);
        if (role == null) {
            throw new RoleNotFoundException("Role '" + ROLE_PROPIETARIO + "' was not found.");
        }
        userModel.setRol(role);
        userModel.setClave(passwordEncoderPort.encode(userModel.getClave()));

        userPersistencePort.saveUser(userModel);
    }

    @Override
    public UserModel findByCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            throw new RequiredFieldException("correo");
        }
        return userPersistencePort.findByCorreo(correo);
    }

    @Override
    public UserModel findById(Long id) {
        if (id == null) {
            throw new RequiredFieldException("id");
        }
        return userPersistencePort.findById(id);
    }

    private void validateRequiredFields(UserModel userModel) {
        if (userModel == null) {
            throw new RequiredFieldException("user");
        }
        if (isNullOrBlank(userModel.getNombre())) {
            throw new RequiredFieldException("nombre");
        }
        if (isNullOrBlank(userModel.getApellido())) {
            throw new RequiredFieldException("apellido");
        }
        if (isNullOrBlank(userModel.getDocumentoDeIdentidad())) {
            throw new RequiredFieldException("documentoDeIdentidad");
        }
        if (isNullOrBlank(userModel.getCelular())) {
            throw new RequiredFieldException("celular");
        }
        if (userModel.getFechaNacimiento() == null) {
            throw new RequiredFieldException("fechaNacimiento");
        }
        if (isNullOrBlank(userModel.getCorreo())) {
            throw new RequiredFieldException("correo");
        }
        if (isNullOrBlank(userModel.getClave())) {
            throw new RequiredFieldException("clave");
        }
    }

    private void validateDocumentFormat(String document) {
        if (!Pattern.matches(NUMERIC_DOCUMENT_REGEX, document)) {
            throw new InvalidDocumentException();
        }
    }

    private void validatePhoneFormat(String phone) {
        if (!Pattern.matches(PHONE_REGEX, phone) || phone.length() > 13) {
            throw new InvalidPhoneException();
        }
    }

    private void validateEmailFormat(String email) {
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new InvalidEmailException();
        }
    }

    private void validateAge(LocalDate birthDate) {
        if (Period.between(birthDate, LocalDate.now()).getYears() < 18) {
            throw new UnderageUserException();
        }
    }

    private void validateUniqueness(String email, String document) {
        if (userPersistencePort.existsByCorreo(email)) {
            throw new UserAlreadyExistsException("A user with email '" + email + "' already exists.");
        }
        if (userPersistencePort.existsByDocumentoDeIdentidad(document)) {
            throw new UserAlreadyExistsException("A user with identity document '" + document + "' already exists.");
        }
    }

    private boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
