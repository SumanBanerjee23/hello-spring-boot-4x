package com.letschange.aot;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "plants")
public record Plant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id,
    
    @NotBlank(message = "Plant name is required")
    @Size(max = 100, message = "Plant name must not exceed 100 characters")
    String name,
    
    @Size(max = 200, message = "Scientific name must not exceed 200 characters")
    String scientificName,
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "9999.99", message = "Price must not exceed 9999.99")
    @Column(precision = 10)
    Double price,
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    Integer stockQuantity
) {
    public Plant {
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (stockQuantity != null && stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }
}
