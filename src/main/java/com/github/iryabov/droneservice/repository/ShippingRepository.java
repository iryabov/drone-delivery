package com.github.iryabov.droneservice.repository;

import com.github.iryabov.droneservice.entity.Shipping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRepository extends CrudRepository<Shipping, Integer> {
}
