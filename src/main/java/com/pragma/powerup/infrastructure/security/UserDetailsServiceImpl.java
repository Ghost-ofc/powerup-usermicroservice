package com.pragma.powerup.infrastructure.security;

import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + correo));

        String roleName = user.getRol().getName();
        return new User(
                user.getCorreo(),
                user.getClave(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}
