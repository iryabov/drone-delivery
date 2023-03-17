package com.github.iryabov.droneservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "package_item")
public class PackageItem {
    @Id
    private Long id;
    @Column(name = "amount")
    private Integer amount;
    @ManyToOne
    @JoinColumn(name ="shipping_id")
    private Shipping shipping;
    @ManyToOne
    @JoinColumn(name ="goods_id")
    private Medication goods;
}
