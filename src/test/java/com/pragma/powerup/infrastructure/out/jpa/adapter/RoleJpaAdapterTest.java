package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.RoleEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRoleEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleJpaAdapterTest {

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private IRoleEntityMapper roleEntityMapper;

    @InjectMocks
    private RoleJpaAdapter roleJpaAdapter;

    @Test
    @DisplayName("Debe buscar rol por id")
    void findById_shouldReturnRole() {
        RoleEntity entity = new RoleEntity(1L, "ROLE_ADMIN", "Admin");
        RoleModel model = new RoleModel(1L, "ROLE_ADMIN", "Admin");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(roleEntityMapper.toModel(entity)).thenReturn(model);

        RoleModel result = roleJpaAdapter.findById(1L);

        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.getName());
        verify(roleRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe buscar rol por nombre")
    void findByName_shouldReturnRole() {
        RoleEntity entity = new RoleEntity(2L, "ROLE_PROPIETARIO", "Propietario");
        RoleModel model = new RoleModel(2L, "ROLE_PROPIETARIO", "Propietario");

        when(roleRepository.findByName("ROLE_PROPIETARIO")).thenReturn(Optional.of(entity));
        when(roleEntityMapper.toModel(entity)).thenReturn(model);

        RoleModel result = roleJpaAdapter.findByName("ROLE_PROPIETARIO");

        assertNotNull(result);
        assertEquals("ROLE_PROPIETARIO", result.getName());
        verify(roleRepository).findByName("ROLE_PROPIETARIO");
    }
}
