package com.suitup.backend.payment;

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
@Table(name = "payment_status_history")
public class PaymentStatusHistoryEntity extends CreatedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 32)
    private PaymentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 32)
    private PaymentStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_user_id")
    private UserEntity changedByUser;

    @Column(columnDefinition = "text")
    private String note;

    public PaymentStatusHistoryEntity() {
    }

    public PaymentStatusHistoryEntity(PaymentStatus oldStatus, PaymentStatus newStatus, UserEntity changedByUser, String note) {
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedByUser = changedByUser;
        this.note = note;
    }

    public PaymentEntity getPayment() { return payment; }
    public void setPayment(PaymentEntity payment) { this.payment = payment; }
    public PaymentStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(PaymentStatus oldStatus) { this.oldStatus = oldStatus; }
    public PaymentStatus getNewStatus() { return newStatus; }
    public void setNewStatus(PaymentStatus newStatus) { this.newStatus = newStatus; }
    public UserEntity getChangedByUser() { return changedByUser; }
    public void setChangedByUser(UserEntity changedByUser) { this.changedByUser = changedByUser; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
