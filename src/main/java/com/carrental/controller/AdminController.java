package com.carrental.controller;

import com.carrental.entity.Car;
import com.carrental.repo.BookingRepository;
import com.carrental.repo.CarRepository;
import com.carrental.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    public AdminController(UserService userService) { this.userService = userService; }
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("cars",            carRepository.findAll());
        model.addAttribute("bookings",        bookingRepository.findAll());
        model.addAttribute("pendingLicenses", userService.pendingLicenses());
        return "admin/dashboard";
    }

//    @GetMapping("/licenses")
//    public String pending(Model m) {
//        m.addAttribute("list", userService.pendingLicenses());
//        return "admin/licenses";
//    }

    @PostMapping("/licenses/{id}/approve")
    public String approve(@PathVariable Long id) {
        userService.approveLicense(id);
        return "redirect:/admin/dashboard";
    }



    @GetMapping("/cars")
    public String listCars(Model model) {
        model.addAttribute("cars", carRepository.findAll());
        return "admin/car_list";
    }

    @GetMapping("/cars/add")
    public String showAddCarForm(Model model) {
        model.addAttribute("car", new Car());
        return "admin/car_form";
    }

    @PostMapping("/cars/add")
    public String addCar(@ModelAttribute Car car,
                         @RequestParam("images") MultipartFile[] images) throws IOException {

        List<String> imagePaths = new ArrayList<>();
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path path = Paths.get("src/main/resources/static/uploads/cars/" + filename);
                Files.write(path, image.getBytes());
                imagePaths.add("uploads/cars/" + filename);
            }
        }

        car.setImagePaths(imagePaths);
        carRepository.save(car);
        return "redirect:/admin/cars";
    }

    @GetMapping("/cars/edit/{id}")
    public String showEditCarForm(@PathVariable Long id, Model model) {
        model.addAttribute("car", carRepository.findById(id).orElseThrow());
        return "admin/car_form";
    }

    @PostMapping("/cars/edit/{id}")
    public String updateCar(@PathVariable Long id,
                            @ModelAttribute Car updatedCar,
                            @RequestParam("images") MultipartFile[] images) throws IOException {

        Car car = carRepository.findById(id).orElseThrow();

        car.setBrand(updatedCar.getBrand());
        car.setModel(updatedCar.getModel());
        car.setYear(updatedCar.getYear());
        car.setPricePerDay(updatedCar.getPricePerDay());
        car.setFuelType(updatedCar.getFuelType());
        car.setSeats(updatedCar.getSeats());
        car.setTransmission(updatedCar.getTransmission());

        List<String> newImages = new ArrayList<>();
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path path = Paths.get("src/main/resources/static/uploads/cars/" + filename);
                Files.write(path, image.getBytes());
                newImages.add("uploads/cars/" + filename);
            }
        }

        if (!newImages.isEmpty()) {
            car.setImagePaths(newImages);
        }

        carRepository.save(car);
        return "redirect:/admin/cars";
    }

    @GetMapping("/cars/delete/{id}")
    public String deleteCar(@PathVariable Long id) {
        carRepository.deleteById(id);
        return "redirect:/admin/cars";
    }
}
