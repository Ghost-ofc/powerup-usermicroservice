package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByCorreo(String correo);
    Optional<UserEntity> findByDocumentoDeIdentidad(String documentoDeIdentidad);
    Boolean existsByCorreo(String correo);
    Boolean existsByDocumentoDeIdentidad(String documentoDeIdentidad);
}
