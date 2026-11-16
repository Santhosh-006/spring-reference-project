# 🏥 WeCARE Spring Boot Project: Complete Execution Flow & Line-by-Line Code Guide

---

## Table of Contents
1. [High-Level Architecture & End-to-End Execution Flow](#1-high-level-architecture--end-to-end-execution-flow)
2. [What Happens During Project Startup (Boot Lifecycle)](#2-what-happens-during-project-startup-boot-lifecycle)
3. [The Complete Request–Response Lifecycle (Step-by-Step)](#3-the-complete-requestresponse-lifecycle-step-by-step)
4. [Line-by-Line Explanation: `WeCareApplication.java`](#4-line-by-line-explanation-wecareapplicationjava)
5. [Line-by-Line Explanation: `Appointment.java` (Model / Entity)](#5-line-by-line-explanation-appointmentjava-model--entity)
6. [Line-by-Line Explanation: `AppointmentRepository.java` (Repository Layer)](#6-line-by-line-explanation-appointmentrepositoryjava-repository-layer)
7. [Line-by-Line Explanation: `AppointmentService.java` (Service Layer)](#7-line-by-line-explanation-appointmentservicejava-service-layer)
8. [Line-by-Line Explanation: `AppointmentController.java` (Controller Layer)](#8-line-by-line-explanation-appointmentcontrollerjava-controller-layer)
9. [Line-by-Line Explanation: `index.html` (Form & UI Layout)](#9-line-by-line-explanation-indexhtml-form--ui-layout)
10. [Line-by-Line Explanation: JavaScript in `index.html`](#10-line-by-line-explanation-javascript-in-indexhtml)
11. [Configuration Files: `application.properties` & `pom.xml`](#11-configuration-files-applicationproperties--pomxml)

---

## 1. High-Level Architecture & End-to-End Execution Flow

This project follows the industry-standard **N-Tier Layered Architecture**:

```
┌─────────────────────────────────────────────────────────────┐
│                       BROWSER (Client)                      │
│               index.html (HTML + CSS + JavaScript)          │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTP Request (JSON)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    SPRING BOOT BACKEND                      │
│                                                             │
│  1. CONTROLLER LAYER (@RestController)                      │
│     - AppointmentController.java                            │
│     - Listens to HTTP endpoints (/api/appointments)         │
│     - Converts JSON ↔ Java Objects                          │
│                              │ Calls method                 │
│                              ▼                              │
│  2. SERVICE LAYER (@Service)                                │
│     - AppointmentService.java                               │
│     - Business logic, validation, defaults                  │
│                              │ Calls method                 │
│                              ▼                              │
│  3. REPOSITORY LAYER (@Repository / JpaRepository)          │
│     - AppointmentRepository.java                            │
│     - Spring Data JPA generates SQL queries automatically   │
│                              │ Translates via Hibernate     │
│                              ▼                              │
│  4. MODEL / ENTITY LAYER (@Entity)                          │
│     - Appointment.java                                      │
│     - Blueprint matching the database table schema          │
└──────────────────────────────┬──────────────────────────────┘
                               │ SQL Query (INSERT, SELECT, UPDATE, DELETE)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                   DATABASE (MySQL Server)                   │
│                     Database: `wecare_db`                   │
│                     Table: `appointments`                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. What Happens During Project Startup (Boot Lifecycle)

When you click **Run** on `WeCareApplication.java`:

1. **Java Virtual Machine (JVM) Boot**: The JVM starts and calls `public static void main(String[] args)`.
2. **Spring Context Initialization**: `SpringApplication.run()` spins up the **Inversion of Control (IoC) Container** (ApplicationContext).
3. **Component Scanning**: Spring scans `com.wecare.app` and its sub-packages for annotations:
   - Finds `@Entity` on `Appointment` $\rightarrow$ Registers it as a JPA entity.
   - Finds `@Repository` on `AppointmentRepository` $\rightarrow$ Generates an implementation bean with database methods.
   - Finds `@Service` on `AppointmentService` $\rightarrow$ Creates the service bean and injects the repository into it via `@Autowired`.
   - Finds `@RestController` on `AppointmentController` $\rightarrow$ Creates the controller bean and maps HTTP routes (`/api/appointments`).
4. **Database Connection & Hibernate Schema Generation**:
   - Spring reads `application.properties` and connects to MySQL on port `3306`.
   - Because `spring.jpa.hibernate.ddl-auto=update` is enabled, Hibernate checks whether the `appointments` table exists in MySQL. If not, Hibernate generates and executes `CREATE TABLE appointments (...)` automatically!
5. **Database Seeding (`CommandLineRunner`)**:
   - The `@Bean CommandLineRunner initDatabase(...)` executes.
   - It checks `repository.count() == 0`. If true, it inserts 2 default sample appointments so the table is never blank during demo.
6. **Embedded Tomcat Server Starts**: Apache Tomcat starts listening on port `8080`.
7. **Static Asset Registration**: Spring registers `src/main/resources/static/` so requests to `http://localhost:8080/` directly serve `index.html`.

---

## 3. The Complete Request–Response Lifecycle (Step-by-Step)

Here is exactly what happens when a user clicks **"Book Appointment"** on the website:

1. **User Interaction**: The user enters patient information into the form and clicks **"Book Appointment"**.
2. **JavaScript Interception**: In `index.html`, `handleFormSubmit(e)` intercepts the event and runs `e.preventDefault()` to stop the browser from reloading the page.
3. **Payload Construction**: JavaScript collects values from input fields and creates a JavaScript object:
   ```javascript
   { patientName: "Rahul Sharma", doctorName: "Dr. Deepa", problem: "Fever", ... }
   ```
4. **HTTP Dispatch**: `fetch('/api/appointments', { method: 'POST', body: JSON.stringify(payload) })` sends an HTTP POST request with `Content-Type: application/json`.
5. **Tomcat DispatcherServlet**: Embedded Tomcat receives the HTTP request and forwards it to Spring’s `DispatcherServlet`.
6. **Controller Mapping**: `DispatcherServlet` matches the path `/api/appointments` and method `POST` to `AppointmentController.createAppointment(...)`.
7. **JSON Deserialization (Jackson)**: Spring's Jackson library converts the JSON string into an `Appointment` Java object and passes it to the `@RequestBody` parameter.
8. **Service Delegation**: The controller calls `appointmentService.saveAppointment(appointment)`.
9. **Business Logic Execution**: The service checks if `status` is empty; if so, it sets `"SCHEDULED"`.
10. **Repository & ORM Execution**: The service calls `appointmentRepository.save(appointment)`. Hibernate creates an SQL statement:
    ```sql
    INSERT INTO appointments (patient_name, doctor_name, problem, appointment_date, phone, status)
    VALUES ('Rahul Sharma', 'Dr. Deepa', 'Fever', '2026-11-20', '9840123456', 'SCHEDULED');
    ```
11. **MySQL Storage**: MySQL inserts the row, assigns a generated auto-increment `id` (e.g. `3`), and commits.
12. **Return Flow**:
    - The repository returns the saved `Appointment` entity with its new `id`.
    - The service returns it to the controller.
    - The controller wraps it in `ResponseEntity<>(saved, HttpStatus.CREATED)` (HTTP 201).
    - Jackson converts the Java `Appointment` object back into JSON.
13. **Browser Reception & UI Update**:
    - Browser `fetch()` receives the response with HTTP status `201`.
    - JavaScript triggers `showAlert("New appointment booked successfully!", "success")`.
    - JavaScript calls `loadAppointments()`, which fetches the latest list and repopulates the table dynamically.

---

## 4. Line-by-Line Explanation: `WeCareApplication.java`

```java
package com.wecare.app;
```
* **Line 1**: Declares the base package name. All classes inside this folder or sub-folders belong to `com.wecare.app`.

```java
import com.wecare.app.model.Appointment;
import com.wecare.app.repository.AppointmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
```
* **Lines 3–8**: Import classes required from the Spring framework and from our own packages.

```java
@SpringBootApplication
public class WeCareApplication {
```
* **Line 10**: `@SpringBootApplication` is a meta-annotation that bundles three vital annotations:
  1. `@SpringBootConfiguration`: Declares this class as a configuration source for beans.
  2. `@EnableAutoConfiguration`: Tells Spring Boot to automatically configure Tomcat, JPA, and MySQL based on `pom.xml`.
  3. `@ComponentScan`: Tells Spring to automatically find and load all classes annotated with `@RestController`, `@Service`, `@Repository`, and `@Entity` within `com.wecare.app`.
* **Line 11**: Defines the public class `WeCareApplication`.

```java
    public static void main(String[] args) {
        SpringApplication.run(WeCareApplication.class, args);
        System.out.println("=================================================");
        System.out.println(">>> WeCARE Application is RUNNING on port 8080! <<<");
        System.out.println(">>> Open UI: http://localhost:8080            <<<");
        System.out.println(">>> API URL: http://localhost:8080/api/appointments");
        System.out.println("=================================================");
    }
```
* **Line 13**: Standard Java entry point.
* **Line 14**: `SpringApplication.run(...)` launches the entire Spring Boot framework and embedded Tomcat server.
* **Lines 15–19**: Prints helpful console messages with direct URLs once the server successfully starts.

```java
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
```
* **Line 26**: `@Bean` registers the returned method object inside Spring's IoC container.
* **Line 27**: `CommandLineRunner` is a functional interface in Spring Boot that runs code **once** immediately after the application starts. Spring automatically injects the `AppointmentRepository` bean as an argument.
* **Line 28**: Java Lambda expression (`args -> { ... }`) executing the startup code.
* **Line 29**: `repository.count() == 0` checks if the MySQL table is empty.
* **Lines 30–31**: Inserts 2 sample appointments using `repository.save(...)` so your project is never empty during examination.
* **Line 32**: Logs a confirmation message to the console.

---

## 5. Line-by-Line Explanation: `Appointment.java` (Model / Entity)

```java
package com.wecare.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
```
* **Lines 1–8**: Imports JPA (Jakarta Persistence API) annotations used for Object-Relational Mapping (ORM).

```java
@Entity
@Table(name = "appointments")
public class Appointment {
```
* **Line 10**: `@Entity` marks this Java class as a persistent database entity. Hibernate maps it to a database table.
* **Line 11**: `@Table(name = "appointments")` specifies the exact table name in MySQL (`appointments`).
* **Line 12**: Declares the `Appointment` POJO (Plain Old Java Object).

```java
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
```
* **Line 14**: `@Id` designates the `id` field as the **Primary Key**.
* **Line 15**: `@GeneratedValue(strategy = GenerationType.IDENTITY)` configures MySQL's native `AUTO_INCREMENT` column behavior.
* **Line 16**: Defines `private Long id;` to hold the unique ID.

```java
    private String patientName;
    private String doctorName;
    private String problem;
    private String appointmentDate;
    private String phone;
    private String status;
```
* **Lines 18–23**: Define the data fields for healthcare appointments. Hibernate maps each variable into a table column (`patient_name`, `doctor_name`, `problem`, etc.) with the `VARCHAR(255)` type.

```java
    public Appointment() {
    }
```
* **Lines 25–26**: **Default No-Argument Constructor**. JPA/Hibernate strictly requires this so it can instantiate objects reflectively when reading rows from the database.

```java
    public Appointment(String patientName, String doctorName, String problem, String appointmentDate, String phone, String status) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.problem = problem;
        this.appointmentDate = appointmentDate;
        this.phone = phone;
        this.status = status;
    }
```
* **Lines 28–35**: **Parameterized Constructor**. Enables manual creation of new appointments (used in `CommandLineRunner` and testing).

```java
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    ...
```
* **Getters and Setters**:
  - **Getters** allow Spring/Jackson to read the private fields and serialize them into JSON sent to the browser.
  - **Setters** allow Spring/Jackson to populate the private fields from incoming JSON payloads sent by the browser.

---

## 6. Line-by-Line Explanation: `AppointmentRepository.java` (Repository Layer)

```java
package com.wecare.app.repository;

import com.wecare.app.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
```
* **Lines 1–6**: Imports the `Appointment` model, `JpaRepository`, and Spring's `@Repository` annotation.

```java
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
```
* **Line 8**: `@Repository` tells Spring that this interface is a Data Access Object (DAO) component.
* **Line 9**: Declares an `interface` (not a class) extending `JpaRepository<Appointment, Long>`:
  - `Appointment`: The Entity type managed by this repository.
  - `Long`: The data type of the entity's primary key (`@Id`).
  - **Why no implementation?** Spring Data JPA automatically generates the SQL implementation in memory at runtime!

```java
    List<Appointment> findByPatientNameContainingIgnoreCase(String patientName);
}
```
* **Line 11**: **Derived Query Method**. By following Spring's naming convention (`findBy + Property + Condition`), Spring automatically parses the method name and generates:
  ```sql
  SELECT * FROM appointments WHERE UPPER(patient_name) LIKE UPPER(CONCAT('%', ?, '%'));
  ```
  No manual SQL query writing needed!

---

## 7. Line-by-Line Explanation: `AppointmentService.java` (Service Layer)

```java
package com.wecare.app.service;

import com.wecare.app.model.Appointment;
import com.wecare.app.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
```
* **Lines 1–9**: Imports necessary classes, including `Optional` for safe null handling.

```java
@Service
public class AppointmentService {
```
* **Line 11**: `@Service` marks this class as a Spring-managed Service component containing business logic.

```java
    @Autowired
    private AppointmentRepository appointmentRepository;
```
* **Line 14**: `@Autowired` performs **Dependency Injection**. Spring finds the repository bean and assigns it to this variable automatically.

```java
    // 1. Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
```
* **Lines 17–19**: Calls `findAll()` on the repository to retrieve all rows from `appointments`.

```java
    // 2. Get single appointment by ID
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }
```
* **Lines 22–24**: Calls `findById(id)`. Returns `Optional<Appointment>`, which prevents `NullPointerException` if the ID doesn't exist.

```java
    // 3. Create / Book a new appointment
    public Appointment saveAppointment(Appointment appointment) {
        if (appointment.getStatus() == null || appointment.getStatus().trim().isEmpty()) {
            appointment.setStatus("SCHEDULED");
        }
        return appointmentRepository.save(appointment);
    }
```
* **Lines 27–32**: 
  - Validates that `status` is not empty. If null/empty, applies the business default `"SCHEDULED"`.
  - Calls `appointmentRepository.save(appointment)` to insert the record into MySQL and returns the saved object.

```java
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
```
* **Lines 35–45**:
  - Searches for the existing record by `id`.
  - If found (`.map(...)`), updates each field with the new values.
  - Calls `.save(existing)` (Hibernate recognizes the ID exists and performs an `UPDATE` query).
  - If not found, returns `null` via `.orElse(null)`.

```java
    // 5. Delete an appointment by ID
    public boolean deleteAppointment(Long id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
```
* **Lines 48–54**: Checks if the ID exists via `existsById(id)`. If yes, deletes it using `deleteById(id)` and returns `true`; otherwise returns `false`.

```java
    // 6. Search appointments by patient name
    public List<Appointment> searchByPatientName(String name) {
        return appointmentRepository.findByPatientNameContainingIgnoreCase(name);
    }
}
```
* **Lines 57–59**: Delegates search requests to the repository's derived method.

---

## 8. Line-by-Line Explanation: `AppointmentController.java` (Controller Layer)

```java
package com.wecare.app.controller;

import com.wecare.app.model.Appointment;
import com.wecare.app.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
```
* **Lines 1–9**: Imports Spring Web annotations (`@RestController`, `@GetMapping`, etc.) and `ResponseEntity`.

```java
@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {
```
* **Line 11**: `@RestController` marks this class as a RESTful web controller. It combines `@Controller` and `@ResponseBody`, meaning method return values are automatically written directly into the HTTP response body as JSON.
* **Line 12**: `@RequestMapping("/api/appointments")` sets the common URL prefix for all endpoints in this class.
* **Line 13**: `@CrossOrigin(origins = "*")` enables Cross-Origin Resource Sharing (CORS), allowing external browsers and applications to call these endpoints without CORS security blocks.

```java
    @Autowired
    private AppointmentService appointmentService;
```
* **Line 16**: Injects the `AppointmentService` bean.

```java
    // 1. GET ALL: GET http://localhost:8080/api/appointments
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> list = appointmentService.getAllAppointments();
        return ResponseEntity.ok(list);
    }
```
* **Lines 19–23**:
  - `@GetMapping`: Handles HTTP GET requests to `/api/appointments`.
  - `ResponseEntity.ok(list)`: Wraps the list in an HTTP 200 (OK) response with the list converted to a JSON array.

```java
    // 2. GET BY ID: GET http://localhost:8080/api/appointments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
```
* **Lines 26–31**:
  - `@GetMapping("/{id}")`: Maps dynamic path variables, e.g. `/api/appointments/5`.
  - `@PathVariable Long id`: Extracts `{id}` from the URL and binds it to `Long id`.
  - If found, returns HTTP 200 with the appointment JSON. If not, returns HTTP 404 (Not Found).

```java
    // 3. CREATE: POST http://localhost:8080/api/appointments
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        Appointment saved = appointmentService.saveAppointment(appointment);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
```
* **Lines 34–38**:
  - `@PostMapping`: Handles HTTP POST requests.
  - `@RequestBody`: Jackson parses the incoming JSON payload from the request body and converts it into an `Appointment` object.
  - `HttpStatus.CREATED`: Returns HTTP 201 (Created) along with the saved entity JSON.

```java
    // 4. UPDATE: PUT http://localhost:8080/api/appointments/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointment) {
        Appointment updated = appointmentService.updateAppointment(id, appointment);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }
```
* **Lines 41–48**:
  - `@PutMapping("/{id}")`: Handles HTTP PUT requests for updating existing resources.
  - Takes both `@PathVariable Long id` and the updated `@RequestBody Appointment`.
  - Returns HTTP 200 with updated JSON if successful, or HTTP 404 if the ID was invalid.

```java
    // 5. DELETE: DELETE http://localhost:8080/api/appointments/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        boolean deleted = appointmentService.deleteAppointment(id);
        if (deleted) {
            return ResponseEntity.ok("Appointment deleted successfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found!");
    }
```
* **Lines 51–58**:
  - `@DeleteMapping("/{id}")`: Handles HTTP DELETE requests.
  - Calls `deleteAppointment(id)` and returns HTTP 200 on success or HTTP 404 if not found.

```java
    // 6. SEARCH: GET http://localhost:8080/api/appointments/search?name=John
    @GetMapping("/search")
    public ResponseEntity<List<Appointment>> searchAppointments(@RequestParam("name") String name) {
        return ResponseEntity.ok(appointmentService.searchByPatientName(name));
    }
}
```
* **Lines 61–64**:
  - `@GetMapping("/search")`: Handles query parameters, e.g. `?name=Rajesh`.
  - `@RequestParam("name")`: Extracts the query string value.

---

## 9. Line-by-Line Explanation: `index.html` (Form & UI Layout)

```html
<form id="appointment-form" onsubmit="handleFormSubmit(event)">
    <input type="hidden" id="appointment-id">
```
* `onsubmit="handleFormSubmit(event)"`: Calls the JavaScript handler whenever the user clicks the submit button or presses Enter.
* `<input type="hidden" id="appointment-id">`: **Crucial design pattern**.
  - When booking a *new* appointment, this input is empty $\rightarrow$ triggers a `POST` request.
  - When *editing* an existing appointment, this input stores the appointment's `id` $\rightarrow$ triggers a `PUT` request!

```html
    <div class="form-group">
        <label for="patientName">Patient Full Name *</label>
        <input type="text" id="patientName" placeholder="e.g. Rahul Sharma" required>
    </div>
```
* `id="patientName"`: Gives JavaScript a unique identifier to read user input via `document.getElementById('patientName').value`.
* `required`: HTML5 client-side validation ensuring the field cannot be submitted blank.

```html
    <div class="form-group">
        <label for="status">Appointment Status</label>
        <select id="status">
            <option value="SCHEDULED">SCHEDULED</option>
            <option value="CONFIRMED">CONFIRMED</option>
            <option value="CANCELLED">CANCELLED</option>
        </select>
    </div>
```
* Dropdown menu ensuring only valid enumerated status values are sent to the backend.

```html
    <div class="btn-group">
        <button type="submit" class="btn-primary" id="submit-btn">Book Appointment</button>
        <button type="button" class="btn-secondary" onclick="resetForm()">Reset</button>
    </div>
</form>
```
* `id="submit-btn"`: The label dynamically changes between *"Book Appointment"* and *"Update Appointment"* when editing.
* `onclick="resetForm()"`: Clears all form fields and resets the hidden ID.

```html
<div class="search-box">
    <input type="text" id="search-input" placeholder="Search by patient name..." onkeyup="filterAppointments()">
    <button class="btn-secondary" onclick="loadAppointments()">Refresh</button>
</div>
```
* `onkeyup="filterAppointments()"`: Fires every time the user types a character in the search box, providing instant live filtering.

```html
<tbody id="appointment-table-body">
    <!-- Populated dynamically by JavaScript -->
</tbody>
```
* Empty table body that JavaScript fills with `<tr>` elements returned from the database.

---

## 10. Line-by-Line Explanation: JavaScript in `index.html`

```javascript
const API_BASE = '/api/appointments';
let allAppointments = [];
```
* `API_BASE`: Stores the base URL for the REST endpoints.
* `allAppointments`: Client-side cache storing the fetched appointments for instant searching and editing.

### 1. `loadAppointments()`
```javascript
async function loadAppointments() {
    try {
        const res = await fetch(API_BASE);
        if (!res.ok) throw new Error("Failed to fetch data");
        allAppointments = await res.json();
        renderTable(allAppointments);
    } catch (err) {
        showAlert("Could not connect to Spring Boot backend: " + err.message, "error");
    }
}
```
* `async/await`: Allows asynchronous network calls without blocking the UI.
* `fetch(API_BASE)`: Sends an HTTP GET request to `http://localhost:8080/api/appointments`.
* `await res.json()`: Parses the JSON response body into a JavaScript array of objects.
* `renderTable(allAppointments)`: Calls the rendering function to draw table rows.

### 2. `renderTable(data)`
```javascript
function renderTable(data) {
    const tbody = document.getElementById('appointment-table-body');
    tbody.innerHTML = ''; // Clears existing rows

    if (data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8">No appointments found...</td></tr>';
        return;
    }

    data.forEach(item => {
        let badgeClass = 'badge-scheduled';
        if (item.status === 'CONFIRMED') badgeClass = 'badge-confirmed';
        if (item.status === 'CANCELLED') badgeClass = 'badge-cancelled';

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>#${item.id}</strong></td>
            <td><strong>${escapeHtml(item.patientName)}</strong></td>
            <td>${escapeHtml(item.doctorName)}</td>
            <td>${escapeHtml(item.problem)}</td>
            <td>${escapeHtml(item.appointmentDate)}</td>
            <td>${escapeHtml(item.phone)}</td>
            <td><span class="badge ${badgeClass}">${escapeHtml(item.status)}</span></td>
            <td>
                <button class="btn-edit" onclick="editAppointment(${item.id})">Edit</button>
                <button class="btn-danger" onclick="deleteAppointment(${item.id})">Delete</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}
```
* Iterates through each appointment in the list.
* Sets CSS classes based on the status (green for `CONFIRMED`, blue for `SCHEDULED`, red for `CANCELLED`).
* Dynamically creates a table row (`<tr>`) containing data cells and action buttons.
* Attaches `onclick="editAppointment(${item.id})"` and `onclick="deleteAppointment(${item.id})"`.

### 3. `handleFormSubmit(e)`
```javascript
async function handleFormSubmit(e) {
    e.preventDefault(); // Prevents browser from reloading page

    const id = document.getElementById('appointment-id').value;
    const payload = {
        patientName: document.getElementById('patientName').value.trim(),
        doctorName: document.getElementById('doctorName').value.trim(),
        problem: document.getElementById('problem').value.trim(),
        appointmentDate: document.getElementById('appointmentDate').value,
        phone: document.getElementById('phone').value.trim(),
        status: document.getElementById('status').value
    };

    try {
        let res;
        if (id) {
            // Edit existing: PUT /api/appointments/{id}
            res = await fetch(`${API_BASE}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
        } else {
            // Create new: POST /api/appointments
            res = await fetch(API_BASE, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
        }

        if (res.ok) {
            showAlert(id ? "Appointment updated successfully!" : "New appointment booked successfully!", "success");
            resetForm();
            loadAppointments();
        }
    } catch (err) {
        showAlert("Error submitting request: " + err.message, "error");
    }
}
```
* Gathers input values into the `payload` JavaScript object.
* Checks if `id` exists:
  - If `id` has a value $\rightarrow$ Sends HTTP `PUT` to `${API_BASE}/${id}` (Update).
  - If `id` is empty $\rightarrow$ Sends HTTP `POST` to `${API_BASE}` (Create).
* On success, resets the form and calls `loadAppointments()` to refresh the table.

### 4. `editAppointment(id)`
```javascript
function editAppointment(id) {
    const item = allAppointments.find(a => a.id === id);
    if (!item) return;

    // Populate form fields with existing data
    document.getElementById('appointment-id').value = item.id;
    document.getElementById('patientName').value = item.patientName;
    document.getElementById('doctorName').value = item.doctorName;
    document.getElementById('problem').value = item.problem;
    document.getElementById('appointmentDate').value = item.appointmentDate;
    document.getElementById('phone').value = item.phone;
    document.getElementById('status').value = item.status;

    // Switch form UI to Update Mode
    document.getElementById('form-title').innerText = `Edit Appointment #${item.id}`;
    document.getElementById('submit-btn').innerText = "Update Appointment";
    window.scrollTo({ top: 0, behavior: 'smooth' });
}
```
* Finds the clicked appointment in `allAppointments`.
* Fills each form field with the appointment's current data.
* Sets the hidden `appointment-id` field so subsequent submission triggers `PUT`.
* Changes the button text to *"Update Appointment"*.

### 5. `deleteAppointment(id)`
```javascript
async function deleteAppointment(id) {
    if (!confirm(`Are you sure you want to delete appointment #${id}?`)) return;

    try {
        const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
        if (res.ok) {
            showAlert(`Appointment #${id} deleted successfully!`, "success");
            loadAppointments(); // Refresh table
        }
    } catch (err) {
        showAlert("Error: " + err.message, "error");
    }
}
```
* Displays a confirmation dialog (`confirm()`).
* If confirmed, sends an HTTP `DELETE` request to `/api/appointments/{id}`.
* Calls `loadAppointments()` to remove the row from the table.

### 6. `filterAppointments()`
```javascript
function filterAppointments() {
    const term = document.getElementById('search-input').value.toLowerCase();
    const filtered = allAppointments.filter(a => 
        a.patientName.toLowerCase().includes(term) ||
        a.doctorName.toLowerCase().includes(term) ||
        a.problem.toLowerCase().includes(term)
    );
    renderTable(filtered);
}
```
* Reads the search text and converts it to lowercase.
* Filters the array across patient name, doctor name, and problem.
* Calls `renderTable()` with the filtered subset for instant UI response.

---

## 11. Configuration Files: `application.properties` & `pom.xml`

### `application.properties`
```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/wecare_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
* `server.port=8080`: Embedded Tomcat listens on port 8080.
* `createDatabaseIfNotExist=true`: Creates `wecare_db` in MySQL if it doesn't already exist.
* `ddl-auto=update`: Hibernate compares `@Entity` classes against MySQL tables and automatically updates columns and tables without deleting data.
* `show-sql=true`: Prints generated SQL statements to the console so examiners can observe Hibernate executing queries.

### `pom.xml` Key Dependencies
```xml
<!-- 1. Spring Boot Web: Embedded Tomcat, Spring MVC, REST controllers, JSON mapping -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- 2. Spring Data JPA: Hibernate ORM, JpaRepository, database management -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- 3. MySQL Connector: JDBC driver facilitating communication with MySQL 8 -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```
