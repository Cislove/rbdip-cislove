package com.rbdip.bookstore.order;

import com.rbdip.bookstore.customer.Customer;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * Намеренно денормализованная сущность: хранит "сырые" контактные данные
 * клиента прямо в заказе вместо ссылки на отдельную таблицу customers.
 * Это цель для нормализации схемы в ЛР2, а поле customerFullName - цель
 * expand-contract миграции в ЛР3 (разбить на firstName/lastName).
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private final Instant createdAt = Instant.now();

    protected Order() {
        // for JPA
    }

    public Order(Customer customer, String status) {
        this.customer = customer;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() { return customer; }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
