package com.github.iryabov.droneservice.mapper;

import com.github.iryabov.droneservice.entity.Medication;
import com.github.iryabov.droneservice.entity.PackageItem;
import com.github.iryabov.droneservice.entity.Shipping;
import com.github.iryabov.droneservice.entity.ShippingLog;
import com.github.iryabov.droneservice.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ShippingMapper {
    public List<PackageItem> toPackageItems(PackageForm form) {
        List<PackageItem> entityItems = new ArrayList<>();
        for (PackageForm.Item formItem : form.getItems()) {
            PackageItem entityItem = new PackageItem();
            Medication goods = new Medication();
            goods.setId(formItem.getGoodsId());
            entityItem.setGoods(goods);
            entityItem.setAmount(formItem.getAmount());
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
            item.setAmount(entityItem.getAmount());
            packageInfo.getItems().add(item);
        }
        return packageInfo;
    }

    public ShippingInfo toInfo(Shipping shipping) {
        ShippingInfo shippingInfo = new ShippingInfo();
        shippingInfo.setId(shipping.getId());
        shippingInfo.setDeliveryStatus(shipping.getStatus());
        return shippingInfo;
    }

    public ShippingLogInfo toInfo(ShippingLog entity) {
        ShippingLogInfo info = new ShippingLogInfo();
        info.setEvent(entity.getEvent());
        info.setTime(entity.getLogTime());
        info.setNewValue(entity.getNewValue());
        return info;
    }
}
