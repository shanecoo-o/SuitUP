package com.suitup.backend.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.suitup.backend.catalog.SuitModelEntity;
import com.suitup.backend.common.persistence.CreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "order_items")
public class OrderItemEntity extends CreatedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suit_model_id")
    private SuitModelEntity suitModel;

    @Column(name = "suit_name_snapshot", nullable = false, length = 200)
    private String suitNameSnapshot;

    @Column(name = "category_snapshot", nullable = false, length = 100)
    private String categorySnapshot;

    @Column(name = "fabric_snapshot", nullable = false, length = 100)
    private String fabricSnapshot;

    @Column(name = "color_snapshot", nullable = false, length = 100)
    private String colorSnapshot;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "design_snapshot", nullable = false, columnDefinition = "jsonb")
    private JsonNode designSnapshot;

    @Column(name = "unit_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "line_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal lineTotal;

    public OrderItemEntity() {
    }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }
    public SuitModelEntity getSuitModel() { return suitModel; }
    public void setSuitModel(SuitModelEntity suitModel) { this.suitModel = suitModel; }
    public String getSuitNameSnapshot() { return suitNameSnapshot; }
    public void setSuitNameSnapshot(String suitNameSnapshot) { this.suitNameSnapshot = suitNameSnapshot; }
    public String getCategorySnapshot() { return categorySnapshot; }
    public void setCategorySnapshot(String categorySnapshot) { this.categorySnapshot = categorySnapshot; }
    public String getFabricSnapshot() { return fabricSnapshot; }
    public void setFabricSnapshot(String fabricSnapshot) { this.fabricSnapshot = fabricSnapshot; }
    public String getColorSnapshot() { return colorSnapshot; }
    public void setColorSnapshot(String colorSnapshot) { this.colorSnapshot = colorSnapshot; }
    public JsonNode getDesignSnapshot() { return designSnapshot; }
    public void setDesignSnapshot(JsonNode designSnapshot) { this.designSnapshot = designSnapshot; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
