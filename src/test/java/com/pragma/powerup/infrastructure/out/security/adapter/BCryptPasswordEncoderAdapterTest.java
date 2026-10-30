package com.pragma.powerup.infrastructure.out.security.adapter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BCryptPasswordEncoderAdapterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BCryptPasswordEncoderAdapter passwordEncoderAdapter;

    @Test
    @DisplayName("Debe delegar la codificación de contraseña al PasswordEncoder")
    void encode_shouldDelegateToPasswordEncoder() {
        when(passwordEncoder.encode("myPassword")).thenReturn("encodedPassword");

        String result = passwordEncoderAdapter.encode("myPassword");

        assertEquals("encodedPassword", result);
        verify(passwordEncoder).encode("myPassword");
    }

    @Test
    @DisplayName("Debe delegar la verificación de coincidencia al PasswordEncoder")
    void matches_shouldDelegateToPasswordEncoder() {
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);

        boolean result = passwordEncoderAdapter.matches("rawPass", "encodedPass");

        assertTrue(result);
        verify(passwordEncoder).matches("rawPass", "encodedPass");
    }
}
