package com.github.iryabov.droneservice.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class PackageForm {
    private List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Item {
        private Integer goodsId;
        private Double amount;
        public Item(Integer goodsId, Double amount) {
            this.goodsId = goodsId;
            this.amount = amount;
        }
    }

}
