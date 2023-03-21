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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "amount", nullable = false)
    private Double amount;
    @ManyToOne
    @JoinColumn(name ="shipping_id", nullable = false)
    private Shipping shipping;
    @ManyToOne
    @JoinColumn(name ="goods_id", nullable = false)
    private Medication goods;
}
