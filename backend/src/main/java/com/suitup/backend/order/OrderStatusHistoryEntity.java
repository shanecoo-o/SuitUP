package com.suitup.backend.order;

import com.suitup.backend.common.persistence.CreatedEntity;
import com.suitup.backend.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_status_history")
public class OrderStatusHistoryEntity extends CreatedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 40)
    private OrderStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 40)
    private OrderStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_user_id")
    private UserEntity changedByUser;

    @Column(columnDefinition = "text")
    private String note;

    public OrderStatusHistoryEntity() {
    }

    public OrderStatusHistoryEntity(OrderStatus oldStatus, OrderStatus newStatus, UserEntity changedByUser, String note) {
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedByUser = changedByUser;
        this.note = note;
    }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }
    public OrderStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(OrderStatus oldStatus) { this.oldStatus = oldStatus; }
    public OrderStatus getNewStatus() { return newStatus; }
    public void setNewStatus(OrderStatus newStatus) { this.newStatus = newStatus; }
    public UserEntity getChangedByUser() { return changedByUser; }
    public void setChangedByUser(UserEntity changedByUser) { this.changedByUser = changedByUser; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
