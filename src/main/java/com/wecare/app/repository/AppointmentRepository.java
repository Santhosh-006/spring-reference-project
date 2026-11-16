package com.wecare.app.repository;

import com.wecare.app.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * REPOSITORY LAYER:
 * What it does:
 * - Directly talks to MySQL database.
 * - By extending JpaRepository<Appointment, Long>, Spring automatically provides:
 *   - save()         -> Insert / Update record
 *   - findAll()      -> Select all records
 *   - findById(id)   -> Find a single record by primary key
 *   - deleteById(id) -> Delete record by primary key
 * - You do NOT need to write any SQL queries manually!
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Custom finder method (Spring automatically converts this into SQL based on the method name!)
    List<Appointment> findByPatientNameContainingIgnoreCase(String patientName);
}
