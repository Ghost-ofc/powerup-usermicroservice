package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;

public interface IUserHandler {
    void saveOwner(UserRequestDto userRequestDto);
    UserResponseDto getByCorreo(String correo);
}
