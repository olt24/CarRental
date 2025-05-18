package com.carrental.controller;

import com.carrental.entity.Booking;
import com.carrental.entity.Car;
import com.carrental.entity.Payment;
import com.carrental.entity.User;
import com.carrental.repo.BookingRepository;
import com.carrental.repo.CarRepository;
import com.carrental.repo.PaymentRepository;
import com.carrental.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    // Home page: show a few featured cars
    @GetMapping("/")
    public String home(Model model) {
        List<Car> all = carRepository.findAll();
        model.addAttribute("cars", all.subList(0, Math.min(6, all.size())));
        return "home";
    }

    // Full catalog with optional filters
    @GetMapping("/cars")
    public String listCars(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer seats,
            @RequestParam(required = false) Double maxPrice,
            Model model
    ) {
        List<Car> cars = carRepository.findAll();

        if (brand != null && !brand.isEmpty()) {
            String low = brand.toLowerCase();
            cars = cars.stream()
                    .filter(c -> c.getBrand().toLowerCase().contains(low))
                    .collect(Collectors.toList());
        }
        if (seats != null) {
            cars = cars.stream()
                    .filter(c -> c.getSeats() == seats)
                    .collect(Collectors.toList());
        }
        if (maxPrice != null) {
            cars = cars.stream()
                    .filter(c -> c.getPricePerDay() <= maxPrice)
                    .collect(Collectors.toList());
        }

        model.addAttribute("cars", cars);
        model.addAttribute("brand", brand);
        model.addAttribute("seats", seats);
        model.addAttribute("maxPrice", maxPrice);
        return "cars";
    }

    @GetMapping("/cars/{id}")
    public String carDetail(@PathVariable Long id, Model model) {
        Car car = carRepository.findById(id).orElseThrow();
        List<Booking> bookings = bookingRepository.findByCar(car);
        List<Map<String,String>> bookedRanges = bookings.stream()
                .map(b -> Map.of(
                        "start", b.getStartDate().toString(),
                        "end",   b.getEndDate().toString()
                ))
                .collect(Collectors.toList());

        model.addAttribute("car", car);
        model.addAttribute("bookedRanges", bookedRanges);
        return "car_detail";
    }

    @PostMapping("/cars/{id}/book")
    public String bookCar(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam Payment.PaymentMethod paymentMethod,
            Principal principal,
            RedirectAttributes flash
    ) {
        Car car = carRepository.findById(id).orElseThrow();

        // **Lookup user by email (principal.getName()) instead of username**
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // 1) Check for date conflicts
        List<Booking> conflicts = bookingRepository
                .findByCarAndEndDateGreaterThanEqualAndStartDateLessThanEqual(car, startDate, endDate);
        if (!conflicts.isEmpty()) {
            flash.addFlashAttribute("error", "That car is already booked for these dates.");
            return "redirect:/cars/" + id;
        }

        // 2) Save booking
        Booking booking = new Booking();
        booking.setCar(car);
        booking.setUser(user);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking = bookingRepository.save(booking);

        // 3) Calculate amount
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal amount = BigDecimal.valueOf(car.getPricePerDay())
                .multiply(BigDecimal.valueOf(days));

        // 4) Save payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(amount);
        payment.setMethod(paymentMethod);

        if (paymentMethod == Payment.PaymentMethod.CASH) {
            // immediate PAID for cash option
            payment.setStatus(Payment.PaymentStatus.PAID);
            paymentRepository.save(payment);
            flash.addFlashAttribute("success",
                    "Booked for " + days + " day(s). Please pay $"
                            + amount + " on pickup.");
            return "redirect:/dashboard";
        } else {
            // ONLINE: pending until demo “Pay Now”
            payment.setStatus(Payment.PaymentStatus.PENDING);
            payment = paymentRepository.save(payment);
            return "redirect:/payment/demo/" + payment.getId();
        }
    }

    // 3) Show the fake “Pay Now” page
    @GetMapping("/payment/demo/{paymentId}")
    public String demoPaymentPage(
            @PathVariable Long paymentId,
            Model model
    ) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment id: " + paymentId));
        model.addAttribute("payment", payment);
        return "demo_payment";
    }


    @PostMapping("/payment/demo/{paymentId}")
    public String doDemoPayment(
            @PathVariable Long paymentId,
            RedirectAttributes flash
    ) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        payment.setStatus(Payment.PaymentStatus.PAID);
        paymentRepository.save(payment);

        flash.addFlashAttribute("success",
                "🎉 Payment of $" + payment.getAmount() + " received! Your booking is confirmed.");
        return "redirect:/dashboard";
    }

    @PostMapping("/booking/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, Principal principal, RedirectAttributes flash) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        String email = principal.getName();

        if (!booking.getUser().getEmail().equals(email)) {
            flash.addFlashAttribute("error", "Unauthorized.");
            return "redirect:/dashboard";
        }

        bookingRepository.deleteById(id);
        flash.addFlashAttribute("success", "Booking cancelled.");
        return "redirect:/dashboard";
    }



    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        List<Booking> bookings = bookingRepository.findByUser(user);
        model.addAttribute("bookings", bookings);
        return "dashboard";
    }
}