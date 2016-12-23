package com.library.app.order.model;

import com.library.app.book.model.Book;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Embeddable
public class OrderItem implements Serializable{
    private static final long serialVersionUID = 3520003746949600860L;
    
    @ManyToOne
    @JoinColumn(name = "book_id")
    @NotNull
    private Book book;
    
    @NotNull
    private Integer quantity;
    
    @NotNull
    private Double price;

    public OrderItem() {
    }

    public OrderItem(Book book, Integer quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    
    public void calculatePrice() {
        if(book != null && quantity != null) {
            price = book.getPrice() * quantity;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.book);
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
        final OrderItem other = (OrderItem) obj;
        if (!Objects.equals(this.book, other.book)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrderItem{" + "book=" + book + ", quantity=" + quantity + ", price=" + price + '}';
    }
    
}
