package com.suitup.backend.auth;

import com.suitup.backend.auth.dto.AuthResponse;
import com.suitup.backend.auth.dto.LoginRequest;
import com.suitup.backend.auth.dto.RefreshTokenRequest;
import com.suitup.backend.auth.dto.RegisterRequest;
import com.suitup.backend.common.DuplicateResourceException;
import com.suitup.backend.common.ResourceNotFoundException;
import com.suitup.backend.security.CustomUserDetails;
import com.suitup.backend.security.JwtService;
import com.suitup.backend.user.RoleCode;
import com.suitup.backend.user.RoleEntity;
import com.suitup.backend.user.RoleRepository;
import com.suitup.backend.user.UserEntity;
import com.suitup.backend.user.UserMapper;
import com.suitup.backend.user.UserRepository;
import com.suitup.backend.user.dto.CurrentUserResponse;
import java.util.Locale;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Já existe uma conta com este email");
        }
        RoleEntity customerRole = roleRepository.findByCode(RoleCode.CUSTOMER)
            .orElseThrow(() -> new ResourceNotFoundException("Role CUSTOMER não configurado"));

        UserEntity user = new UserEntity(
            request.fullName().trim(),
            email,
            trimToNull(request.phone()),
            passwordEncoder.encode(request.password())
        );
        user.setEnabled(true);
        user.addRole(customerRole);
        return tokens(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
            .orElseThrow(InvalidCredentialsException::new);
        if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return tokens(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshTokenRequest request) {
        UUID userId;
        try {
            userId = jwtService.extractUserId(jwtService.parseRefreshToken(request.refreshToken()));
        } catch (RuntimeException exception) {
            throw new InvalidCredentialsException("Refresh token inválido ou expirado");
        }
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new InvalidCredentialsException("Refresh token inválido ou expirado"));
        if (!user.isEnabled()) {
            throw new InvalidCredentialsException("A conta está desactivada");
        }
        return tokens(user);
    }

    @Transactional(readOnly = true)
    public CurrentUserResponse me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new InvalidCredentialsException("Sessão inválida");
        }
        UserEntity user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new InvalidCredentialsException("Sessão inválida"));
        return userMapper.toCurrentUser(user);
    }

    private AuthResponse tokens(UserEntity user) {
        return new AuthResponse(
            jwtService.generateAccessToken(user),
            jwtService.generateRefreshToken(user),
            "Bearer",
            jwtService.accessTokenExpiresInSeconds(),
            userMapper.toCurrentUser(user)
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
