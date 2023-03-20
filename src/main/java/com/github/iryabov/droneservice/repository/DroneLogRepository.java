package com.github.iryabov.droneservice.repository;

import com.github.iryabov.droneservice.entity.DroneEvent;
import com.github.iryabov.droneservice.entity.DroneLog;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DroneLogRepository extends JpaRepository<DroneLog, Long> {

    List<DroneLog> findAllByDroneIdAndLogTimeBetweenAndEvent(Integer droneId, LocalDateTime logTimeStart, LocalDateTime logTimeEnd, DroneEvent event);
}
