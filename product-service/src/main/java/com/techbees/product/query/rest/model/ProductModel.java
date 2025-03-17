package com.techbees.product.query.rest.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductModel {

    private String productId;

    private String title;

    private BigDecimal price;

    private Integer quantity;
}
