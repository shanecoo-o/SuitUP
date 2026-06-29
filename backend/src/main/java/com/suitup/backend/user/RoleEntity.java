package com.suitup.backend.user;

import com.suitup.backend.common.persistence.UuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class RoleEntity extends UuidEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 32)
    private RoleCode code;

    @Column(nullable = false, length = 100)
    private String name;

    public RoleEntity() {
    }

    public RoleEntity(RoleCode code, String name) {
        this.code = code;
        this.name = name;
    }

    public RoleCode getCode() {
        return code;
    }

    public void setCode(RoleCode code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
