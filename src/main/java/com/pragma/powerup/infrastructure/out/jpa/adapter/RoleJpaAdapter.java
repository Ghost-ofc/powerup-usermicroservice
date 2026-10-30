package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.domain.spi.IRolePersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRoleEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleJpaAdapter implements IRolePersistencePort {

    private final IRoleRepository roleRepository;
    private final IRoleEntityMapper roleEntityMapper;

    @Override
    public RoleModel findById(Long id) {
        return roleRepository.findById(id)
                .map(roleEntityMapper::toModel)
                .orElse(null);
    }

    @Override
    public RoleModel findByName(String name) {
        return roleRepository.findByName(name)
                .map(roleEntityMapper::toModel)
                .orElse(null);
    }
}
