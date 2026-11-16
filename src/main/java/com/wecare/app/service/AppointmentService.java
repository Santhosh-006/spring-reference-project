package com.wecare.app.service;

import com.wecare.app.model.Appointment;
import com.wecare.app.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * SERVICE LAYER:
 * What it does:
 * - Contains the core Business Logic of your application.
 * - Sits between the Controller (API) and the Repository (Database).
 * - Handles data validation, transformations, calculations, and rules before saving to DB.
 */
@Service
public class AppointmentService {

    // Dependency Injection: Spring injects the repository instance automatically
    @Autowired
    private AppointmentRepository appointmentRepository;

    // 1. Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // 2. Get single appointment by ID
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    // 3. Create / Book a new appointment
    public Appointment saveAppointment(Appointment appointment) {
        // Business logic check: Set default status if not provided
        if (appointment.getStatus() == null || appointment.getStatus().trim().isEmpty()) {
            appointment.setStatus("SCHEDULED");
        }
        return appointmentRepository.save(appointment);
    }

    // 4. Update an existing appointment
    public Appointment updateAppointment(Long id, Appointment updatedDetails) {
        return appointmentRepository.findById(id).map(existing -> {
            existing.setPatientName(updatedDetails.getPatientName());
            existing.setDoctorName(updatedDetails.getDoctorName());
            existing.setProblem(updatedDetails.getProblem());
            existing.setAppointmentDate(updatedDetails.getAppointmentDate());
            existing.setPhone(updatedDetails.getPhone());
            existing.setStatus(updatedDetails.getStatus());
            return appointmentRepository.save(existing);
        }).orElse(null);
    }

    // 5. Delete an appointment by ID
    public boolean deleteAppointment(Long id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 6. Search appointments by patient name
    public List<Appointment> searchByPatientName(String name) {
        return appointmentRepository.findByPatientNameContainingIgnoreCase(name);
    }
}
