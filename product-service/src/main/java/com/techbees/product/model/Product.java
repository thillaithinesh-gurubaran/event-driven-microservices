package com.techbees.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Product {

    //@NotBlank(message="Product title is required")
    private String title;

    @Min(value=1, message="Price cannot be lower than 1")
    private BigDecimal price;

    @Min(value=1, message="Quantity cannot be lower than 1")
    @Max(value=5, message="Quantity cannot be larger than 5")
    private Integer quantity;

}
