package com.techbees.order.service.repository;

import com.techbees.order.service.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String>  {
    OrderEntity findByOrderId(String orderId);
}
