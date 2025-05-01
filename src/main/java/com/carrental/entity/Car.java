package com.carrental.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "cars")
public class Car {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String make;
    private String model;
    private String plateNumber;
    private boolean available = true;

    // Optionally, you could track bookings here:
    // @OneToMany(mappedBy = "car")
    // private Set<Booking> bookings;

    // getters & setters …
}

