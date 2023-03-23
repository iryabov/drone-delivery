package com.github.iryabov.dronedelivery.repository;

import com.github.iryabov.dronedelivery.entity.ShippingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingLogRepository extends JpaRepository<ShippingLog, Long> {
    List<ShippingLog> findAllByShippingId(int shippingId);
}
