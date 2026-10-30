package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.UserModel;

public interface IUserPersistencePort {
    void saveUser(UserModel userModel);
    UserModel findByCorreo(String correo);
    UserModel findById(Long id);
    boolean existsByCorreo(String correo);
    boolean existsByDocumentoDeIdentidad(String documentoDeIdentidad);
}
