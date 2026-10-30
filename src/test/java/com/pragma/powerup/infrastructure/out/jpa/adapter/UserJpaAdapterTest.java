package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserJpaAdapterTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IUserEntityMapper userEntityMapper;

    @InjectMocks
    private UserJpaAdapter userJpaAdapter;

    @Test
    @DisplayName("Debe guardar un usuario a través del repositorio")
    void saveUser_shouldMapAndSave() {
        UserModel userModel = new UserModel();
        UserEntity userEntity = new UserEntity();

        when(userEntityMapper.toEntity(userModel)).thenReturn(userEntity);

        userJpaAdapter.saveUser(userModel);

        verify(userEntityMapper).toEntity(userModel);
        verify(userRepository).save(userEntity);
    }

    @Test
    @DisplayName("Debe buscar usuario por correo")
    void findByCorreo_shouldReturnUser() {
        UserEntity userEntity = new UserEntity();
        UserModel userModel = new UserModel();

        when(userRepository.findByCorreo("test@correo.com")).thenReturn(Optional.of(userEntity));
        when(userEntityMapper.toModel(userEntity)).thenReturn(userModel);

        UserModel result = userJpaAdapter.findByCorreo("test@correo.com");

        assertNotNull(result);
        verify(userRepository).findByCorreo("test@correo.com");
    }

    @Test
    @DisplayName("Debe buscar usuario por id")
    void findById_shouldReturnUser() {
        UserEntity userEntity = new UserEntity();
        UserModel userModel = new UserModel();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userEntityMapper.toModel(userEntity)).thenReturn(userModel);

        UserModel result = userJpaAdapter.findById(1L);

        assertNotNull(result);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe verificar existencia por correo y documento")
    void existsChecks_shouldReturnBoolean() {
        when(userRepository.existsByCorreo("test@correo.com")).thenReturn(true);
        when(userRepository.existsByDocumentoDeIdentidad("12345")).thenReturn(true);

        assertTrue(userJpaAdapter.existsByCorreo("test@correo.com"));
        assertTrue(userJpaAdapter.existsByDocumentoDeIdentidad("12345"));
    }
}
