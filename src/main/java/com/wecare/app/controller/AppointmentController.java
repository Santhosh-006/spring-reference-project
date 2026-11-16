package com.wecare.app.controller;

import com.wecare.app.model.Appointment;
import com.wecare.app.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLLER LAYER:
 * What it does:
 * - The Receptionist / Entry point of your REST API.
 * - Listens for HTTP requests (GET, POST, PUT, DELETE) from the Frontend or Postman.
 * - Calls the Service layer to do the actual work.
 * - Returns data as JSON format with proper HTTP Status codes (200 OK, 201 Created, 404 Not Found).
 */
@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*") // Allows web browsers / frontend to connect without CORS issues
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // 1. GET ALL APPOINTMENTS: GET http://localhost:8080/api/appointments
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> list = appointmentService.getAllAppointments();
        return ResponseEntity.ok(list);
    }

    // 2. GET APPOINTMENT BY ID: GET http://localhost:8080/api/appointments/1
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. CREATE / BOOK APPOINTMENT: POST http://localhost:8080/api/appointments
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        Appointment saved = appointmentService.saveAppointment(appointment);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // 4. UPDATE APPOINTMENT: PUT http://localhost:8080/api/appointments/1
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointment) {
        Appointment updated = appointmentService.updateAppointment(id, appointment);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    // 5. DELETE APPOINTMENT: DELETE http://localhost:8080/api/appointments/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        boolean deleted = appointmentService.deleteAppointment(id);
        if (deleted) {
            return ResponseEntity.ok("Appointment deleted successfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found!");
    }

    // 6. SEARCH BY PATIENT NAME: GET http://localhost:8080/api/appointments/search?name=John
    @GetMapping("/search")
    public ResponseEntity<List<Appointment>> searchAppointments(@RequestParam("name") String name) {
        return ResponseEntity.ok(appointmentService.searchByPatientName(name));
    }
}
