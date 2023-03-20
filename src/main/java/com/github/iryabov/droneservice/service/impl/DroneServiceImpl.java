package com.github.iryabov.droneservice.service.impl;

import com.github.iryabov.droneservice.entity.Drone;
import com.github.iryabov.droneservice.entity.DroneModel;
import com.github.iryabov.droneservice.entity.DroneState;
import com.github.iryabov.droneservice.mapper.DroneMapper;
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

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class DroneServiceImpl implements DroneService {
    private DroneRepository droneRepo;
    private DroneMapper droneMapper;

    @Override
    public int create(DroneRegistrationForm registrationForm) {
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
    public List<DroneLogInfo> getEventLogs(int droneId, LocalDateTime from, LocalDateTime till) {
        throw new UnsupportedOperationException();
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

}
