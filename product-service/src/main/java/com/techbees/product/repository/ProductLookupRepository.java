package com.techbees.product.repository;

import com.techbees.product.entity.ProductEntity;
import com.techbees.product.entity.ProductLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLookupRepository extends JpaRepository<ProductLookupEntity, String> {

    ProductLookupEntity findByProductIdOrTitle(String productId, String title);

}
