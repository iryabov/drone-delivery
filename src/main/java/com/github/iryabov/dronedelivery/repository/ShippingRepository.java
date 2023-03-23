package com.github.iryabov.dronedelivery.repository;

import com.github.iryabov.dronedelivery.entity.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Integer> {
}
