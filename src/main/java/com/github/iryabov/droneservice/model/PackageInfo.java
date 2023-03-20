package com.github.iryabov.droneservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PackageInfo {
    private List<PackageInfo.Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Item {
        private String goodsName;
        private Integer amount;

    }
}
