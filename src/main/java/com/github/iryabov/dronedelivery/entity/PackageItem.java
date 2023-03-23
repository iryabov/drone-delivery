package com.github.iryabov.dronedelivery.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "package_item")
public class PackageItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name ="shipping_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Shipping shipping;
    @ManyToOne
    @JoinColumn(name ="goods_id", nullable = false)
    private Medication goods;
}
