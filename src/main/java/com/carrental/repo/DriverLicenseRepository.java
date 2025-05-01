package com.carrental.repo;

import com.carrental.entity.DriverLicense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverLicenseRepository extends JpaRepository<DriverLicense, Long> {
    List<DriverLicense> findByStatus(com.carrental.entity.DriverLicense.LicenseStatus status);
}
