package com.techbees.product.query.rest;

import com.techbees.product.query.FindProductQuery;
import com.techbees.product.query.rest.model.ProductModel;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductQueryController {

    @Autowired
    QueryGateway queryGateway;
    @GetMapping
    public List<ProductModel> getProducts() {
        FindProductQuery findProductQuery = new FindProductQuery();
        List<ProductModel> products = queryGateway.query(findProductQuery,
                ResponseTypes.multipleInstancesOf(ProductModel.class)).join();
        return products;
    }
}
