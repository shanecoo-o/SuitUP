package com.suitup.backend.security;

import com.suitup.backend.user.UserRepository;
import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(email)
            .map(CustomUserDetails::from)
            .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado"));
    }

    @Transactional(readOnly = true)
    public CustomUserDetails loadById(UUID id) {
        return userRepository.findById(id)
            .map(CustomUserDetails::from)
            .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado"));
    }
}
