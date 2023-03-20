package com.github.iryabov.droneservice.repository;

import com.github.iryabov.droneservice.entity.DroneLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DroneLogRepository extends JpaRepository<DroneLog, Long> {
}
