package com.github.iryabov.droneservice.repository;

import com.github.iryabov.droneservice.entity.DroneLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DroneLogRepository extends CrudRepository<DroneLog, Long> {
}
