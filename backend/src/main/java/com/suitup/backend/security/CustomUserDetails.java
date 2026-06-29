package com.suitup.backend.security;

import com.suitup.backend.user.RoleEntity;
import com.suitup.backend.user.UserEntity;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final UUID id;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String passwordHash;
    private final boolean enabled;
    private final List<GrantedAuthority> authorities;

    private CustomUserDetails(UserEntity user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.passwordHash = user.getPasswordHash();
        this.enabled = user.isEnabled();
        this.authorities = user.getRoles().stream()
            .map(RoleEntity::getCode)
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .map(GrantedAuthority.class::cast)
            .toList();
    }

    public static CustomUserDetails from(UserEntity user) {
        return new CustomUserDetails(user);
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() { return passwordHash; }
    @Override
    public String getUsername() { return email; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return enabled; }
}
