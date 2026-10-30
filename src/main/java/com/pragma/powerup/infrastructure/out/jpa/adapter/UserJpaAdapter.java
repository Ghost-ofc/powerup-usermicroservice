package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements IUserPersistencePort {

    private final IUserRepository userRepository;
    private final IUserEntityMapper userEntityMapper;

    @Override
    public void saveUser(UserModel userModel) {
        UserEntity userEntity = userEntityMapper.toEntity(userModel);
        userRepository.save(userEntity);
    }

    @Override
    public UserModel findByCorreo(String correo) {
        return userRepository.findByCorreo(correo)
                .map(userEntityMapper::toModel)
                .orElse(null);
    }

    @Override
    public UserModel findById(Long id) {
        return userRepository.findById(id)
                .map(userEntityMapper::toModel)
                .orElse(null);
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return Boolean.TRUE.equals(userRepository.existsByCorreo(correo));
    }

    @Override
    public boolean existsByDocumentoDeIdentidad(String documentoDeIdentidad) {
        return Boolean.TRUE.equals(userRepository.existsByDocumentoDeIdentidad(documentoDeIdentidad));
    }
}
