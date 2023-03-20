package com.github.iryabov.droneservice.repository;

import com.github.iryabov.droneservice.entity.ShippingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingLogRepository extends JpaRepository<ShippingLog, Long> {
    List<ShippingLog> findAllByShippingId(int shippingId);
}
