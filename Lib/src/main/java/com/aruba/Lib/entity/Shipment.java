package com.aruba.Lib.entity;

import com.aruba.Lib.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "shipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "status", nullable = false)
    private ShipmentStatus status;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp created;

    @Column(name = "modified", nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp modified;


    public Shipment(Long id, Order order,String note) {
        this.id = id;
        this.order = order;
        this.status = ShipmentStatus.NEW;
        this.note = note;
    }

    public Shipment(Long id, Order order, ShipmentStatus status, String note) {
        this.id = id;
        this.order = order;
        this.status = status;
        this.note = note;
    }
}
