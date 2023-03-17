package com.github.iryabov.droneservice.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class PackageForm {
    private List<Item> items;
    private double totalWeight;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Item {
        public Item(Integer goodsId, Integer amount) {
            this.goodsId = goodsId;
            this.amount = amount;
        }
        private Integer goodsId;
        private Integer amount;
    }

}
