package com.library.app.order.model;

import com.library.app.order.model.Order.OrderStatus;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Embeddable
public class OrderHistoryEntry implements Serializable {
    private static final long serialVersionUID = -5544853563085399050L;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    @NotNull
    private Date createdAt;

    public OrderHistoryEntry() {
    }

    public OrderHistoryEntry(OrderStatus status) {
        this.status = status;
        this.createdAt = new Date();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.status);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrderHistoryEntry other = (OrderHistoryEntry) obj;
        if (this.status != other.status) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrderHistoryEntry{" + "status=" + status + ", createdAt=" + createdAt + '}';
    }
    
}
