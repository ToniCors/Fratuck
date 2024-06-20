package com.aruba.Lib.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "Warehouse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse implements Serializable {


    @Id
    @Column(name = "product_id")
    private Long productId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "product_id")
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @Column(name = "stock")
    private Long stock;

    public Warehouse(Long productId, Long stock) {
        this.productId = productId;
        this.stock = stock;
    }
}
