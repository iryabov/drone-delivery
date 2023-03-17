package com.github.iryabov.droneservice.repository;

import com.github.iryabov.droneservice.entity.ShippingLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingLogRepository extends CrudRepository<ShippingLog, Long> {
}
