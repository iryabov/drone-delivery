package com.github.iryabov.dronedelivery.repository;

import com.github.iryabov.dronedelivery.enums.DroneEvent;
import com.github.iryabov.dronedelivery.entity.DroneLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DroneLogRepository extends JpaRepository<DroneLog, Long> {

    List<DroneLog> findAllByDroneIdAndLogTimeBetweenAndEvent(Integer droneId,
                                                             LocalDateTime logTimeStart,
                                                             LocalDateTime logTimeEnd,
                                                             DroneEvent event);
}
