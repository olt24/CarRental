package com.carrental.entity;

import jakarta.persistence.*;

@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String password;

    private String driverLicensePath;
    private String role; // "USER", "ADMIN"
}
