package com.lognex.productrest.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @Id
    private UUID id;
    @Size(min = 1, max = 255, message = "Product name must be from 1 to 255 characters")
    @NotNull(message = "Name cannot be null")
    private String name;
    @Size(max = 4096, message = "Product description cannot be more than 4096 characters")
    private String description;
    private BigDecimal price;
    private boolean availability;

    public Product() {}

    public Product(UUID id, String name, String description, BigDecimal price, boolean availability) {
        this.id = id;
        this.name = name;
        this.description = description;
        // Кажется, это стоит убрать. Логику переопределения суммы перенести в контроллер и разобраться, почему не работает.
        if (price == null) {
            this.price = BigDecimal.ZERO;
        } else {
            this.price = price;
        }
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
