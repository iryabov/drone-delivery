package com.github.iryabov.dronedelivery.service.impl;

import com.github.iryabov.dronedelivery.entity.*;
import com.github.iryabov.dronedelivery.enums.DroneEvent;
import com.github.iryabov.dronedelivery.enums.DroneModel;
import com.github.iryabov.dronedelivery.enums.DroneState;
import com.github.iryabov.dronedelivery.exception.DroneDeliveryException;
import com.github.iryabov.dronedelivery.mapper.DroneMapper;
import com.github.iryabov.dronedelivery.model.DroneBriefInfo;
import com.github.iryabov.dronedelivery.model.DroneDetailedInfo;
import com.github.iryabov.dronedelivery.model.DroneLogInfo;
import com.github.iryabov.dronedelivery.model.DroneRegistrationForm;
import com.github.iryabov.dronedelivery.repository.DroneLogRepository;
import com.github.iryabov.dronedelivery.repository.DroneRepository;
import com.github.iryabov.dronedelivery.service.DroneService;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.github.iryabov.dronedelivery.validation.ValidateUtil.validate;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class DroneServiceImpl implements DroneService {
    private DroneRepository droneRepo;
    private DroneLogRepository droneLogRepo;
    private DroneMapper droneMapper;
    private Validator validator;

    @Override
    public int create(DroneRegistrationForm registrationForm) {
        validate(validator, registrationForm);
        validateUniqueSerial(registrationForm.getSerial());
        Drone created = droneRepo.save(droneMapper.toEntity(registrationForm));
        return created.getId();
    }

    @Override
    public void delete(int droneId) {
        droneRepo.deleteById(droneId);
    }

    @Override
    public DroneDetailedInfo getDetailedInfo(int droneId) {
        Drone drone = droneRepo.findById(droneId).orElseThrow();
        return droneMapper.toDetailedInfo(drone);
    }

    @Override
    public List<DroneLogInfo> getEventLogs(int droneId, LocalDateTime from, LocalDateTime till, DroneEvent event) {
        List<DroneLogInfo> logs = droneLogRepo.findAllByDroneIdAndLogTimeBetweenAndEvent(droneId,
                        from != null ? from : LocalDateTime.now().minus(1, ChronoUnit.HOURS),
                        till != null ? till : LocalDateTime.now(),
                        event)
                .stream()
                .map(droneMapper::toDroneLogInfo)
                .collect(toList());
        for (int i = 1; i < logs.size(); i++) {
            logs.get(i).setOldValue(logs.get(i - 1).getNewValue());
        }
        return logs;
    }

    @Override
    public List<DroneBriefInfo> getAllByStateAndModel(DroneState state, DroneModel model) {
        Drone criteria = new Drone();
        criteria.setState(state);
        criteria.setModel(model);
        return droneRepo.findAll(Example.of(criteria)).stream().map(droneMapper::toBriefInfo).collect(toList());
    }

    @Override
    public List<DroneBriefInfo> getAllWithLowBattery() {
        return droneRepo.findAllByBatteryLevelLessThan(25).stream().map(droneMapper::toBriefInfo).collect(toList());
    }

    private void validateUniqueSerial(String serial) {
        Drone criteria = new Drone();
        criteria.setSerial(serial);
        if (droneRepo.findOne(Example.of(criteria)).isPresent())
            throw new DroneDeliveryException(String.format("Drone with serial %s already registered", serial));
    }
}
