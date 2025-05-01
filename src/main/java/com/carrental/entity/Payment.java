package com.carrental.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;  // e.g. ONLINE, CASH

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;  // e.g. PENDING, PAID, FAILED

    private LocalDateTime timestamp = LocalDateTime.now();

    // getters & setters …

    public enum PaymentMethod {
        ONLINE, CASH
    }
    public enum PaymentStatus {
        PENDING, PAID, FAILED
    }
}
