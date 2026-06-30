package com.suitup.backend.catalog;

import com.suitup.backend.common.persistence.AuditableEntity;
import com.suitup.backend.upload.UploadedFileEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "suit_models")
public class SuitModelEntity extends AuditableEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, columnDefinition = "text")
    private String description = "";

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency = "MZN";

    @Column(name = "fabric_type", nullable = false, length = 100)
    private String fabricType;

    @Column(nullable = false, length = 100)
    private String color;

    @Column(name = "image_key", length = 150)
    private String imageKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_image_file_id")
    private UploadedFileEntity primaryImageFile;

    @Column(nullable = false)
    private boolean active = true;

    public SuitModelEntity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFabricType() {
        return fabricType;
    }

    public void setFabricType(String fabricType) {
        this.fabricType = fabricType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public UploadedFileEntity getPrimaryImageFile() {
        return primaryImageFile;
    }

    public void setPrimaryImageFile(UploadedFileEntity primaryImageFile) {
        this.primaryImageFile = primaryImageFile;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
