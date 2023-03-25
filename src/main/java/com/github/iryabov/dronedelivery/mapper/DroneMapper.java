package com.github.iryabov.dronedelivery.mapper;

import com.github.iryabov.dronedelivery.entity.*;
import com.github.iryabov.dronedelivery.enums.DroneState;
import com.github.iryabov.dronedelivery.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DroneMapper {
    public DroneBriefInfo toBriefInfo(Drone entity) {
        DroneBriefInfo info = new DroneBriefInfo();
        info.setId(entity.getId());
        info.setName(entity.getModel() + "-" + entity.getSerial());
        info.setState(entity.getState());
        info.setBatteryLevel(entity.getBatteryLevel());
        return info;
    }


    public Drone toEntity(DroneRegistrationForm form) {
        Drone entity = new Drone();
        entity.setSerial(form.getSerial());
        entity.setModel(form.getModel());
        entity.setState(DroneState.IDLE);
        entity.setBatteryLevel(100);
        return entity;
    }

    public DroneDetailedInfo toDetailedInfo(Drone drone) {
        DroneDetailedInfo info = new DroneDetailedInfo();
        info.setId(drone.getId());
        info.setSerial(drone.getSerial());
        info.setDroneModel(drone.getModel());
        info.setName(drone.getModel() + "-" + drone.getSerial());
        info.setState(drone.getState());
        info.setBatteryLevel(drone.getBatteryLevel());
        info.setCurrentLocation(drone.getLocation());
        info.setWeightLimit(drone.getModel().getWeightCapacity());
        if (drone.getShipping() != null) {
            info.setShipping(toShippingBriefInfo(drone.getShipping(), new ShippingBriefInfo()));
        }
        return info;
    }

    public DroneLogInfo toDroneLogInfo(DroneLog entity) {
        DroneLogInfo info = new DroneLogInfo();
        info.setTime(entity.getLogTime());
        info.setEvent(entity.getEvent());
        info.setNewValue(entity.getNewValue());
        return info;
    }

    public List<PackageItem> toPackageItems(PackageForm form, Shipping backlink) {
        List<PackageItem> entityItems = new ArrayList<>();
        for (PackageForm.Item formItem : form.getItems()) {
            PackageItem entityItem = new PackageItem();
            Medication goods = new Medication();
            goods.setId(formItem.getGoodsId());
            entityItem.setGoods(goods);
            entityItem.setQuantity(formItem.getQuantity());
            entityItem.setShipping(backlink);
            entityItems.add(entityItem);
        }
        return entityItems;
    }

    public PackageInfo toPackageInfo(List<PackageItem> entityItems) {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.setItems(new ArrayList<>());
        for (PackageItem entityItem : entityItems) {
            PackageInfo.Item item = new PackageInfo.Item();
            item.setGoodsName(entityItem.getGoods().getName());
            item.setQuantity(entityItem.getQuantity());
            packageInfo.getItems().add(item);
        }
        packageInfo.setTotalWeight(calcTotalWeight(entityItems));
        return packageInfo;
    }

    public <T extends ShippingBriefInfo> T toShippingBriefInfo(Shipping shipping, T info) {
        info.setId(shipping.getId());
        info.setDeliveryStatus(shipping.getStatus());
        info.setDestination(shipping.getDestination());
        info.setDeliveryAddress(shipping.getDeliveryAddress());
        return info;
    }

    public ShippingLogInfo toLogInfo(ShippingLog entity) {
        ShippingLogInfo info = new ShippingLogInfo();
        info.setEvent(entity.getEvent());
        info.setTime(entity.getLogTime());
        info.setNewValue(entity.getNewValue());
        return info;
    }

    private double calcTotalWeight(List<PackageItem> items) {
        return items.stream().map(i -> i.getGoods().getWeight() * i.getQuantity()).reduce(0.0, Double::sum);
    }
}
