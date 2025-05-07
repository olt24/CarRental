package com.carrental.repo;

import com.carrental.entity.Booking;
import com.carrental.entity.Car;
import com.carrental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCarAndEndDateGreaterThanEqualAndStartDateLessThanEqual(
            Car car, LocalDate reqStart, LocalDate reqEnd);

    List<Booking> findByCar(Car car);
    List<Booking> findByUser(User user);
}
