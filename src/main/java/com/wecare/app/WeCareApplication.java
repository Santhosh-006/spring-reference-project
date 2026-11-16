package com.wecare.app;

import com.wecare.app.model.Appointment;
import com.wecare.app.repository.AppointmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * MAIN ENTRY POINT OF SPRING BOOT APPLICATION:
 * What @SpringBootApplication does:
 * 1. @Configuration: Tells Spring this class can define beans.
 * 2. @EnableAutoConfiguration: Tells Spring Boot to auto-configure Tomcat, Database, JPA based on pom.xml.
 * 3. @ComponentScan: Automatically scans this package (com.wecare.app) and all sub-packages
 *    for @RestController, @Service, @Repository, and @Entity annotations.
 */
@SpringBootApplication
public class WeCareApplication {

    public static void main(String[] args) {
        // Starts the embedded Apache Tomcat server (by default on port 8080)
        SpringApplication.run(WeCareApplication.class, args);
        System.out.println("=================================================");
        System.out.println(">>> WeCARE Application is RUNNING on port 8080! <<<");
        System.out.println(">>> Open UI: http://localhost:8080            <<<");
        System.out.println(">>> API URL: http://localhost:8080/api/appointments");
        System.out.println("=================================================");
    }

    /**
     * OPTIONAL BONUS (Great for Lab Demo):
     * Inserts 2 sample appointments automatically when the app boots up,
     * so your database and UI table are never empty during presentation!
     */
    @Bean
    CommandLineRunner initDatabase(AppointmentRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Appointment("Rajesh Kumar", "Dr. A. Sharma (Cardiology)", "Chest pain and routine ECG", "2026-11-20", "9876543210", "CONFIRMED"));
                repository.save(new Appointment("Priya Sundaram", "Dr. V. Menon (Neurology)", "Severe migraine consultation", "2026-11-22", "9123456789", "SCHEDULED"));
                System.out.println(">>> Sample healthcare data loaded successfully! <<<");
            }
        };
    }
}
