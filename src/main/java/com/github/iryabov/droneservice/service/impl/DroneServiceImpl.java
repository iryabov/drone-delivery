package com.github.iryabov.droneservice.service.impl;

import com.github.iryabov.droneservice.entity.Drone;
import com.github.iryabov.droneservice.entity.DroneModel;
import com.github.iryabov.droneservice.entity.DroneState;
import com.github.iryabov.droneservice.model.DroneBriefInfo;
import com.github.iryabov.droneservice.model.DroneDetailedInfo;
import com.github.iryabov.droneservice.model.DroneLogInfo;
import com.github.iryabov.droneservice.model.DroneRegistrationForm;
import com.github.iryabov.droneservice.repository.DroneRepository;
import com.github.iryabov.droneservice.service.DroneService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class DroneServiceImpl implements DroneService {
    private DroneRepository droneRepo;

    @Override
    public int create(DroneRegistrationForm registrationForm) {
        Drone created = droneRepo.save(toEntity(registrationForm));
        return created.getId();
    }

    @Override
    public void delete(int droneId) {
        droneRepo.deleteById(droneId);
    }

    @Override
    public DroneDetailedInfo getDetailedInfo(int droneId) {
        Drone drone = droneRepo.findById(droneId).orElseThrow();
        return toDetailedInfo(drone);
    }

    @Override
    public List<DroneLogInfo> getEventLogs(int droneId, LocalDateTime from, LocalDateTime till) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<DroneBriefInfo> getAllByStateAndModel(DroneState state, DroneModel model) {
        Drone criteria = new Drone();
        criteria.setState(state);
        criteria.setModel(model);
        return droneRepo.findAll(Example.of(criteria)).stream().map(this::toBriefInfo).collect(toList());
    }

    @Override
    public List<DroneBriefInfo> getAllWithLowBattery() {
        return droneRepo.findAllByBatteryLevelLessThan(25).stream().map(this::toBriefInfo).collect(toList());
    }

    private Drone toEntity(DroneRegistrationForm form) {
        Drone entity = new Drone();
        entity.setSerial(form.getSerial());
        entity.setModel(form.getModel());
        return entity;
    }

    private DroneDetailedInfo toDetailedInfo(Drone drone) {
        DroneDetailedInfo info = new DroneDetailedInfo();
        info.setId(drone.getId());
        info.setSerial(drone.getSerial());
        info.setDroneModel(drone.getModel());
        info.setName(drone.getModel() + "-" + drone.getSerial());
        info.setState(drone.getState());
        info.setBatteryLevel(drone.getBatteryLevel());
        return info;
    }

    private DroneBriefInfo toBriefInfo(Drone drone) {
        DroneBriefInfo info = new DroneBriefInfo();
        info.setId(drone.getId());
        info.setName(drone.getModel() + "-" + drone.getSerial());
        info.setState(drone.getState());
        info.setBatteryLevel(drone.getBatteryLevel());
        return info;
    }
}
