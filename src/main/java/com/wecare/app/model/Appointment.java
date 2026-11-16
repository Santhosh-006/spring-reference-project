package com.wecare.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * MODEL / ENTITY LAYER:
 * What it does:
 * - Represents the "Appointment" table in the MySQL database.
 * - Each variable below represents a COLUMN in the database table.
 * - Each instance (object) of this class represents a ROW in the table.
 */
@Entity
@Table(name = "appointments")
public class Appointment {

    // Primary key with Auto-Increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;
    private String doctorName;
    private String problem;
    private String appointmentDate;
    private String phone;
    private String status; // e.g., "SCHEDULED", "COMPLETED", "CANCELLED"

    // Default No-Argument Constructor (Required by JPA/Hibernate to create objects from DB)
    public Appointment() {
    }

    // Parameterized Constructor (Used when creating a new appointment manually)
    public Appointment(String patientName, String doctorName, String problem, String appointmentDate, String phone, String status) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.problem = problem;
        this.appointmentDate = appointmentDate;
        this.phone = phone;
        this.status = status;
    }

    // --- GETTERS AND SETTERS ---
    // (Spring uses Getters to convert Java objects into JSON, and Setters to read incoming JSON)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
