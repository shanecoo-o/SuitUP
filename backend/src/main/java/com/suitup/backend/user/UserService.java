package com.suitup.backend.user;

import com.suitup.backend.common.DuplicateResourceException;
import com.suitup.backend.common.ResourceNotFoundException;
import com.suitup.backend.user.dto.UserSummaryResponse;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public UserEntity requireById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public UserEntity requireByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(normalizeEmail(email))
            .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));
    }

    @Transactional
    public UserSummaryResponse createUser(
        String fullName,
        String email,
        String phone,
        String passwordHash,
        RoleCode roleCode
    ) {
        String normalizedEmail = normalizeEmail(email);
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateResourceException("Já existe um utilizador com este email");
        }

        RoleEntity role = roleRepository.findByCode(roleCode)
            .orElseThrow(() -> new ResourceNotFoundException("Role não configurado: " + roleCode));
        UserEntity user = new UserEntity(fullName.trim(), normalizedEmail, trimToNull(phone), passwordHash);
        user.addRole(role);
        return userMapper.toSummary(userRepository.save(user));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
