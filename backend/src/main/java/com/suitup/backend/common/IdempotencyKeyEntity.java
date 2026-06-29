package com.suitup.backend.common;

import com.suitup.backend.common.persistence.CreatedEntity;
import com.suitup.backend.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKeyEntity extends CreatedEntity {

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 150)
    private String key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "request_hash", nullable = false, length = 128)
    private String requestHash;

    @Column(name = "resource_type", nullable = false, length = 80)
    private String resourceType;

    @Column(name = "resource_id")
    private UUID resourceId;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    public IdempotencyKeyEntity() {
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
    public String getRequestHash() { return requestHash; }
    public void setRequestHash(String requestHash) { this.requestHash = requestHash; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public UUID getResourceId() { return resourceId; }
    public void setResourceId(UUID resourceId) { this.resourceId = resourceId; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
}
