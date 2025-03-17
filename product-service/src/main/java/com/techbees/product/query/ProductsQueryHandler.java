package com.techbees.product.query;

import com.techbees.product.entity.ProductEntity;
import com.techbees.product.query.rest.model.ProductModel;
import com.techbees.product.repository.ProductRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductsQueryHandler {

    private final ProductRepository productRepository;
    public ProductsQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @QueryHandler
    public List<ProductModel> findProducts(FindProductQuery findProductQuery) {
        List<ProductModel> products = new ArrayList<>();

        List<ProductEntity> storedProducts = productRepository.findAll();
        for(ProductEntity entity : storedProducts) {
            ProductModel model = new ProductModel();
            BeanUtils.copyProperties(entity, model);
            products.add(model);
        }
        return products;
    }
}
