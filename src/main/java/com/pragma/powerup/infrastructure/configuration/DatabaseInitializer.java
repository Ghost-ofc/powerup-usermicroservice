package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.infrastructure.out.jpa.entity.RoleEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRoleRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        RoleEntity adminRole = createRoleIfNotFound("ROLE_ADMINISTRADOR", "Administrador de la plataforma");
        createRoleIfNotFound("ROLE_PROPIETARIO", "Propietario de restaurante");
        createRoleIfNotFound("ROLE_EMPLEADO", "Empleado de restaurante");
        createRoleIfNotFound("ROLE_CLIENTE", "Cliente del restaurante");

        if (!userRepository.existsByCorreo("admin@pragma.com")) {
            UserEntity adminUser = new UserEntity();
            adminUser.setNombre("Admin");
            adminUser.setApellido("Principal");
            adminUser.setDocumentoDeIdentidad("1000000001");
            adminUser.setCelular("+573001234567");
            adminUser.setFechaNacimiento(LocalDate.of(1990, 1, 1));
            adminUser.setCorreo("admin@pragma.com");
            adminUser.setClave(passwordEncoder.encode("admin123"));
            adminUser.setRol(adminRole);
            userRepository.save(adminUser);
        }
    }

    private RoleEntity createRoleIfNotFound(String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(new RoleEntity(null, name, description)));
    }
}
