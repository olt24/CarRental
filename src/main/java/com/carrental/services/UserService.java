package com.carrental.services;


import com.carrental.entity.DriverLicense;
import com.carrental.repo.DriverLicenseRepository;
import com.carrental.repo.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.carrental.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepo;
    private final DriverLicenseRepository dlRepo;
    private final PasswordEncoder passwordEncoder;
    private final Path uploadDir = Paths.get("uploads/licenses");

    public UserService(UserRepository userRepo,
                       DriverLicenseRepository dlRepo,
                       PasswordEncoder passwordEncoder) throws IOException {
        this.userRepo = userRepo;
        this.dlRepo = dlRepo;
        this.passwordEncoder = passwordEncoder;
        Files.createDirectories(uploadDir);
    }

    public User register(String name,
                         String email,
                         String rawPassword,
                         MultipartFile dlFile) throws IOException {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole("ROLE_USER");
        u.setEnabled(false);

        DriverLicense dl = new DriverLicense();
        String fn = email + "_" + dlFile.getOriginalFilename();
        Path target = uploadDir.resolve(fn);
        Files.copy(dlFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        dl.setFilePath(fn);
        dl.setStatus(DriverLicense.LicenseStatus.PENDING);
        dl.setUser(u);
        u.setDriverLicense(dl);

        return userRepo.save(u);
    }

    public List<DriverLicense> pendingLicenses() {
        return dlRepo.findByStatus(DriverLicense.LicenseStatus.PENDING);
    }

    public void approveLicense(Long dlId) {
        DriverLicense dl = dlRepo.findById(dlId)
                .orElseThrow(() -> new RuntimeException("DL not found"));
        dl.setStatus(DriverLicense.LicenseStatus.APPROVED);
        dl.getUser().setEnabled(true);
        dlRepo.save(dl);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var auth = List.of(new SimpleGrantedAuthority(u.getRole()));
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPassword(),
                u.isEnabled(), true, true, true,
                auth
        );
    }
}