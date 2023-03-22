package com.github.iryabov.droneservice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PackageForm {
    @NotEmpty(message = "Package items is required")
    @Valid
    private List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Item {
        @NotNull(message = "Goods is required")
        private Integer goodsId;
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        private Integer quantity;
        public Item(Integer goodsId, Integer quantity) {
            this.goodsId = goodsId;
            this.quantity  = quantity;
        }
    }

}
