package com.github.iryabov.dronedelivery.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PackageInfo {
    private List<PackageInfo.Item> items;
    private Double totalWeight;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Item {
        private String goodsName;
        private Integer quantity;

    }
}
