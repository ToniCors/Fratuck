package com.aruba.Lib.entity;

import com.aruba.Lib.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "`order`")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private List<OrderItem> orderItems;

    @Column(name = "total", nullable = false, precision = 20, scale = 6)
    private BigDecimal total;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "payment_id")
//    @JsonIgnore
    private Payment payment;

    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp created;

    @Column(name = "modified", nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp modified;

    public Order(Long id, User user, List<OrderItem> orderItems, BigDecimal total, Payment payment, OrderStatus status, String note) {
        this.id = id;
        this.user = user;
        this.orderItems = orderItems;
        this.total = total;
        this.payment = payment;
        this.status = status;
        this.note = note;
    }

    public Order(Long id, Payment payment) {
        this.id = id;
        this.payment = payment;
    }

    public Order(Long id) {
        this.id = id;
    }
}
