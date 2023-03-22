package com.github.iryabov.droneservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Package items")
    @NotEmpty(message = "Package items is required")
    @Valid
    private List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Item {
        @Schema(description = "Identifier of goods (medication)", example = "1")
        @NotNull(message = "Goods is required")
        private Integer goodsId;
        @Schema(description = "Quantity of goods (medications)", example = "5")
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        private Integer quantity;
        public Item(Integer goodsId, Integer quantity) {
            this.goodsId = goodsId;
            this.quantity  = quantity;
        }
    }

}
