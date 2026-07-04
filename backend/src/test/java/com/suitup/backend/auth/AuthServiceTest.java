package com.suitup.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.suitup.backend.auth.dto.LoginRequest;
import com.suitup.backend.auth.dto.RegisterRequest;
import com.suitup.backend.common.DuplicateResourceException;
import com.suitup.backend.security.JwtService;
import com.suitup.backend.user.RoleCode;
import com.suitup.backend.user.RoleEntity;
import com.suitup.backend.user.RoleRepository;
import com.suitup.backend.user.UserEntity;
import com.suitup.backend.user.UserMapper;
import com.suitup.backend.user.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private JwtService jwtService;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder(4);
        authService = new AuthService(
            userRepository,
            roleRepository,
            passwordEncoder,
            jwtService,
            new UserMapper()
        );
    }

    @Test
    void registrationNormalizesEmailHashesPasswordAndAssignsCustomerOnly() {
        RoleEntity customer = role(RoleCode.CUSTOMER);
        when(userRepository.existsByEmailIgnoreCase("cliente@example.com")).thenReturn(false);
        when(roleRepository.findByCode(RoleCode.CUSTOMER)).thenReturn(Optional.of(customer));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });
        when(jwtService.generateAccessToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");
        when(jwtService.accessTokenExpiresInSeconds()).thenReturn(900L);

        authService.register(new RegisterRequest(
            "  Joao Cliente  ",
            " Cliente@Example.COM ",
            " +258840000001 ",
            "Password123!"
        ));

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity saved = captor.getValue();
        assertThat(saved.getFullName()).isEqualTo("Joao Cliente");
        assertThat(saved.getEmail()).isEqualTo("cliente@example.com");
        assertThat(saved.getPhone()).isEqualTo("+258840000001");
        assertThat(saved.getPasswordHash()).isNotEqualTo("Password123!");
        assertThat(passwordEncoder.matches("Password123!", saved.getPasswordHash())).isTrue();
        assertThat(saved.getRoles()).extracting(RoleEntity::getCode)
            .containsExactly(RoleCode.CUSTOMER);
    }

    @Test
    void registrationRejectsDuplicateEmail() {
        when(userRepository.existsByEmailIgnoreCase("cliente@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest(
            "Joao Cliente", "cliente@example.com", null, "Password123!"
        ))).isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void loginRejectsWrongPasswordAndDisabledAccounts() {
        UserEntity user = new UserEntity(
            "Joao Cliente",
            "cliente@example.com",
            null,
            passwordEncoder.encode("correct-password")
        );
        user.setId(UUID.randomUUID());
        when(userRepository.findByEmailIgnoreCase("cliente@example.com"))
            .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(
            new LoginRequest("cliente@example.com", "wrong-password")
        )).isInstanceOf(InvalidCredentialsException.class);

        user.setEnabled(false);
        assertThatThrownBy(() -> authService.login(
            new LoginRequest("cliente@example.com", "correct-password")
        )).isInstanceOf(InvalidCredentialsException.class);
    }

    private RoleEntity role(RoleCode code) {
        RoleEntity role = new RoleEntity(code, code.name());
        role.setId(UUID.randomUUID());
        return role;
    }
}
