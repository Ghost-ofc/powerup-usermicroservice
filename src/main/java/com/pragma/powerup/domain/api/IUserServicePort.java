package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.UserModel;

public interface IUserServicePort {
    void saveOwner(UserModel userModel);
    UserModel findByCorreo(String correo);
    UserModel findById(Long id);
}
