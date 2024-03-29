package com.lognex.productrest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.UUID;
@Entity
public class Product {

    @Id
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean availability;

    public Product() {}

    public Product(UUID id, String name, String description, BigDecimal price, boolean availability) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.availability = availability;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", availability=" + availability +
                '}';
    }
}
