package com.pragma.powerup.domain.usecase;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IRolePersistencePort rolePersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private UserModel validUser;
    private RoleModel ownerRole;

    @BeforeEach
    void setUp() {
        ownerRole = new RoleModel(2L, UserUseCase.ROLE_PROPIETARIO, "Propietario de restaurante");
        validUser = new UserModel(
                null,
                "Carlos",
                "Gómez",
                "1098765432",
                "+573005698325",
                LocalDate.now().minusYears(25),
                "carlos.gomez@restaurante.com",
                "Password123!",
                null
        );
    }

    @Test
    @DisplayName("Debe guardar exitosamente al propietario cuando todos los datos son válidos")
    void saveOwner_whenValidOwnerData_shouldSaveSuccessfully() {
        when(userPersistencePort.existsByCorreo(validUser.getCorreo())).thenReturn(false);
        when(userPersistencePort.existsByDocumentoDeIdentidad(validUser.getDocumentoDeIdentidad())).thenReturn(false);
        when(rolePersistencePort.findByName(UserUseCase.ROLE_PROPIETARIO)).thenReturn(ownerRole);
        when(passwordEncoderPort.encode("Password123!")).thenReturn("$2a$10$encodedPasswordHash");

        userUseCase.saveOwner(validUser);

        verify(passwordEncoderPort).encode("Password123!");
        verify(userPersistencePort).saveUser(validUser);
        assertEquals("$2a$10$encodedPasswordHash", validUser.getClave());
        assertEquals(ownerRole, validUser.getRol());
    }

    @Test
    @DisplayName("Debe guardar exitosamente cuando el usuario tiene exactamente 18 años")
    void saveOwner_whenUserIsExactly18YearsOld_shouldSaveSuccessfully() {
        validUser.setFechaNacimiento(LocalDate.now().minusYears(18));
        when(userPersistencePort.existsByCorreo(anyString())).thenReturn(false);
        when(userPersistencePort.existsByDocumentoDeIdentidad(anyString())).thenReturn(false);
        when(rolePersistencePort.findByName(UserUseCase.ROLE_PROPIETARIO)).thenReturn(ownerRole);
        when(passwordEncoderPort.encode(anyString())).thenReturn("hashed");

        userUseCase.saveOwner(validUser);

        verify(userPersistencePort).saveUser(validUser);
    }

    @Test
    @DisplayName("Debe lanzar UnderageUserException cuando el usuario es menor de 18 años")
    void saveOwner_whenUserIsUnderage_shouldThrowUnderageUserException() {
        validUser.setFechaNacimiento(LocalDate.now().minusYears(17));

        assertThrows(UnderageUserException.class, () -> userUseCase.saveOwner(validUser));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345ABC", "12-345", "ABCDEF", "123.456"})
    @DisplayName("Debe lanzar InvalidDocumentException si el documento no es estrictamente numérico")
    void saveOwner_whenDocumentNotNumeric_shouldThrowInvalidDocumentException(String invalidDoc) {
        validUser.setDocumentoDeIdentidad(invalidDoc);

        assertThrows(InvalidDocumentException.class, () -> userUseCase.saveOwner(validUser));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"+57300569832599", "+12345678901234", "12345678901234"})
    @DisplayName("Debe lanzar InvalidPhoneException si el celular tiene más de 13 caracteres")
    void saveOwner_whenPhoneExceeds13Chars_shouldThrowInvalidPhoneException(String longPhone) {
        validUser.setCelular(longPhone);

        assertThrows(InvalidPhoneException.class, () -> userUseCase.saveOwner(validUser));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"celular123", "+57 300 569", "++57300", "+57-300"})
    @DisplayName("Debe lanzar InvalidPhoneException si el celular tiene formato no válido")
    void saveOwner_whenPhoneFormatInvalid_shouldThrowInvalidPhoneException(String invalidPhone) {
        validUser.setCelular(invalidPhone);

        assertThrows(InvalidPhoneException.class, () -> userUseCase.saveOwner(validUser));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"correo-invalido", "correo@", "@dominio.com", "correo@dominio", "correo@.com"})
    @DisplayName("Debe lanzar InvalidEmailException si el correo no tiene formato válido")
    void saveOwner_whenEmailFormatInvalid_shouldThrowInvalidEmailException(String invalidEmail) {
        validUser.setCorreo(invalidEmail);

        assertThrows(InvalidEmailException.class, () -> userUseCase.saveOwner(validUser));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Debe lanzar UserAlreadyExistsException cuando el correo ya existe")
    void saveOwner_whenEmailAlreadyExists_shouldThrowUserAlreadyExistsException() {
        when(userPersistencePort.existsByCorreo(validUser.getCorreo())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userUseCase.saveOwner(validUser));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Debe lanzar UserAlreadyExistsException cuando el documento de identidad ya existe")
    void saveOwner_whenDocumentAlreadyExists_shouldThrowUserAlreadyExistsException() {
        when(userPersistencePort.existsByCorreo(validUser.getCorreo())).thenReturn(false);
        when(userPersistencePort.existsByDocumentoDeIdentidad(validUser.getDocumentoDeIdentidad())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userUseCase.saveOwner(validUser));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Debe lanzar RoleNotFoundException cuando el rol propietario no existe en base de datos")
    void saveOwner_whenRoleNotFound_shouldThrowRoleNotFoundException() {
        when(userPersistencePort.existsByCorreo(anyString())).thenReturn(false);
        when(userPersistencePort.existsByDocumentoDeIdentidad(anyString())).thenReturn(false);
        when(rolePersistencePort.findByName(UserUseCase.ROLE_PROPIETARIO)).thenReturn(null);

        assertThrows(RoleNotFoundException.class, () -> userUseCase.saveOwner(validUser));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Debe lanzar RequiredFieldException cuando falta algún campo obligatorio")
    void saveOwner_whenRequiredFieldMissing_shouldThrowRequiredFieldException() {
        UserModel userWithoutName = new UserModel(null, null, "Gómez", "123", "+57300", LocalDate.now().minusYears(20), "c@c.com", "pass", null);
        assertThrows(RequiredFieldException.class, () -> userUseCase.saveOwner(userWithoutName));

        UserModel userWithoutLastName = new UserModel(null, "Carlos", "   ", "123", "+57300", LocalDate.now().minusYears(20), "c@c.com", "pass", null);
        assertThrows(RequiredFieldException.class, () -> userUseCase.saveOwner(userWithoutLastName));

        UserModel userWithoutDoc = new UserModel(null, "Carlos", "Gómez", null, "+57300", LocalDate.now().minusYears(20), "c@c.com", "pass", null);
        assertThrows(RequiredFieldException.class, () -> userUseCase.saveOwner(userWithoutDoc));

        UserModel userWithoutPhone = new UserModel(null, "Carlos", "Gómez", "123", "", LocalDate.now().minusYears(20), "c@c.com", "pass", null);
        assertThrows(RequiredFieldException.class, () -> userUseCase.saveOwner(userWithoutPhone));

        UserModel userWithoutBirthDate = new UserModel(null, "Carlos", "Gómez", "123", "+57300", null, "c@c.com", "pass", null);
        assertThrows(RequiredFieldException.class, () -> userUseCase.saveOwner(userWithoutBirthDate));

        UserModel userWithoutEmail = new UserModel(null, "Carlos", "Gómez", "123", "+57300", LocalDate.now().minusYears(20), null, "pass", null);
        assertThrows(RequiredFieldException.class, () -> userUseCase.saveOwner(userWithoutEmail));

        UserModel userWithoutPassword = new UserModel(null, "Carlos", "Gómez", "123", "+57300", LocalDate.now().minusYears(20), "c@c.com", " ", null);
        assertThrows(RequiredFieldException.class, () -> userUseCase.saveOwner(userWithoutPassword));

        assertThrows(RequiredFieldException.class, () -> userUseCase.saveOwner(null));
    }

    @Test
    @DisplayName("Debe consultar usuario por correo exitosamente")
    void findByCorreo_whenValidEmail_shouldReturnUser() {
        when(userPersistencePort.findByCorreo("carlos.gomez@restaurante.com")).thenReturn(validUser);

        UserModel result = userUseCase.findByCorreo("carlos.gomez@restaurante.com");

        assertNotNull(result);
        assertEquals("carlos.gomez@restaurante.com", result.getCorreo());
        verify(userPersistencePort).findByCorreo("carlos.gomez@restaurante.com");
    }

    @Test
    @DisplayName("Debe consultar usuario por ID exitosamente")
    void findById_whenValidId_shouldReturnUser() {
        when(userPersistencePort.findById(1L)).thenReturn(validUser);

        UserModel result = userUseCase.findById(1L);

        assertNotNull(result);
        verify(userPersistencePort).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar RequiredFieldException cuando se busca por id nulo")
    void findById_whenIdNull_shouldThrowRequiredFieldException() {
        assertThrows(RequiredFieldException.class, () -> userUseCase.findById(null));
    }
}
