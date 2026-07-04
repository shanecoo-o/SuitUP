package com.suitup.backend.measurement;

import com.suitup.backend.common.persistence.CreatedEntity;
import com.suitup.backend.order.OrderEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "measurements")
public class MeasurementEntity extends CreatedEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private OrderEntity order;

    @Column(name = "height_cm", nullable = false, precision = 7, scale = 2)
    private BigDecimal heightCm;
    @Column(name = "chest_cm", nullable = false, precision = 7, scale = 2)
    private BigDecimal chestCm;
    @Column(name = "waist_cm", nullable = false, precision = 7, scale = 2)
    private BigDecimal waistCm;
    @Column(name = "shoulders_cm", nullable = false, precision = 7, scale = 2)
    private BigDecimal shouldersCm;
    @Column(name = "sleeve_cm", nullable = false, precision = 7, scale = 2)
    private BigDecimal sleeveCm;
    @Column(name = "trouser_length_cm", nullable = false, precision = 7, scale = 2)
    private BigDecimal trouserLengthCm;
    @Column(name = "neck_cm", precision = 7, scale = 2)
    private BigDecimal neckCm;
    @Column(name = "hip_cm", precision = 7, scale = 2)
    private BigDecimal hipCm;
    @Column(columnDefinition = "text")
    private String notes;

    public MeasurementEntity() {
    }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }
    public BigDecimal getHeightCm() { return heightCm; }
    public void setHeightCm(BigDecimal heightCm) { this.heightCm = heightCm; }
    public BigDecimal getChestCm() { return chestCm; }
    public void setChestCm(BigDecimal chestCm) { this.chestCm = chestCm; }
    public BigDecimal getWaistCm() { return waistCm; }
    public void setWaistCm(BigDecimal waistCm) { this.waistCm = waistCm; }
    public BigDecimal getShouldersCm() { return shouldersCm; }
    public void setShouldersCm(BigDecimal shouldersCm) { this.shouldersCm = shouldersCm; }
    public BigDecimal getSleeveCm() { return sleeveCm; }
    public void setSleeveCm(BigDecimal sleeveCm) { this.sleeveCm = sleeveCm; }
    public BigDecimal getTrouserLengthCm() { return trouserLengthCm; }
    public void setTrouserLengthCm(BigDecimal trouserLengthCm) { this.trouserLengthCm = trouserLengthCm; }
    public BigDecimal getNeckCm() { return neckCm; }
    public void setNeckCm(BigDecimal neckCm) { this.neckCm = neckCm; }
    public BigDecimal getHipCm() { return hipCm; }
    public void setHipCm(BigDecimal hipCm) { this.hipCm = hipCm; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
