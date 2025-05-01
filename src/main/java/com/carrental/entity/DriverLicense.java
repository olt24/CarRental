package com.carrental.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "driver_licenses")
public class DriverLicense {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String filePath;          // where the PDF/image is stored

    @Enumerated(EnumType.STRING)
    private LicenseStatus status;     // e.g. PENDING, APPROVED, REJECTED

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LicenseStatus getStatus() {
        return status;
    }

    public void setStatus(LicenseStatus status) {
        this.status = status;
    }

    public enum LicenseStatus {
        PENDING, APPROVED, REJECTED
    }
}
