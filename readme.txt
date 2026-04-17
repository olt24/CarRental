# Car Rental Web Application

A full-stack car rental web application built using Spring Boot, Thymeleaf, and MySQL. This project was developed as a bachelor thesis and demonstrates a complete booking system with authentication, payments, and admin management.

--------------------------------------------------

FEATURES

User Features:
- User registration and login
- Upload driver’s license for verification
- Browse available cars
- Book cars for specific time periods
- Prevent double booking (date-based availability)
- Online payment or cash on pickup
- User dashboard:
  - View current bookings
  - View booking history

Admin Features:
- Manage cars (add, update, delete)
- Manage users
- View all bookings
- Control system availability

--------------------------------------------------

TECH STACK

- Backend: Spring Boot
- Frontend: Thymeleaf, HTML, CSS
- Database: MySQL
- Build Tool: Maven
- ORM: JPA / Hibernate

--------------------------------------------------

ARCHITECTURE

- MVC Pattern (Model-View-Controller)
- Layered structure:
  - Controller layer
  - Service layer
  - Repository layer
- Server-side rendering using Thymeleaf

--------------------------------------------------

SETUP & INSTALLATION

1. Clone the repository:
   git clone https://github.com/olt24/CarRental.git

2. Open the project in IntelliJ or Eclipse

3. Configure MySQL database:
   Create a database (e.g. car_rental)

   Update application.properties:
   spring.datasource.url=jdbc:mysql://localhost:3306/car_rental
   spring.datasource.username=YOUR_USERNAME
   spring.datasource.password=YOUR_PASSWORD

4. Run the application:
   mvn spring-boot:run

5. Open in browser:
   http://localhost:8080

--------------------------------------------------

KEY CONCEPTS

- Session management
- Authentication and authorization
- File upload handling (driver’s license)
- Booking logic with date validation
- Database relationships (users, cars, bookings)
- Payment flow integration (basic)

--------------------------------------------------

FUTURE IMPROVEMENTS

- REST API version
- JWT authentication
- Payment gateway integration (Stripe/PayPal)
- Role-based access control
- Deployment with Docker or Cloud

--------------------------------------------------

AUTHOR

Oltion Kotorri
Software Engineering Student @ UNYT
