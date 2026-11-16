# 🏥 WeCARE - Spring REST Healthcare Domain Capstone Project

A complete, production-ready Full Stack Spring Boot 3 + MySQL + HTML/CSS/JS application designed specifically for **Laboratory Exams & Viva Voce**.

---

## ⚡ Quick Start in 3 Steps (Under 2 Minutes)

### Step 1: Open MySQL and create the database
Open **MySQL Command Line Client** or **MySQL Workbench** and run:
```sql
CREATE DATABASE IF NOT EXISTS wecare_db;
```
*(Note: `application.properties` also includes `createDatabaseIfNotExist=true`, so even if you forget, Spring Boot will attempt to create it automatically!)*

### Step 2: Open project in VS Code
1. Open **VS Code**.
2. Go to **File -> Open Folder...** and select this folder:
   `f:\zClg\Naan-Mudhalvan\Spring boot\we_care`
3. Wait 15-30 seconds for the Java Extension to load the dependencies from `pom.xml`.

### Step 3: Run the Application
- Navigate to: `src/main/java/com/wecare/app/WeCareApplication.java`
- Click the **`Run`** button located right above `public static void main(String[] args)`.
- Open your browser at: **[http://localhost:8080](http://localhost:8080)**

---

## 🧠 The 4-Layer Architecture Explained (Viva / Lab Questions)

| Layer | Class / File | What it does in Simple Words |
| :--- | :--- | :--- |
| **Model / Entity** | `Appointment.java` | **The Table Blueprint:** Defines the columns (`patientName`, `doctorName`, `problem`, `status`) in MySQL using `@Entity` and `@Table`. |
| **Repository** | `AppointmentRepository.java` | **The Database Butler:** Extends `JpaRepository`. Provides built-in SQL operations (`save`, `findAll`, `findById`, `deleteById`) without writing any SQL! |
| **Service** | `AppointmentService.java` | **The Brain / Logic:** Handles business checks (e.g. default status = "SCHEDULED"), validations, and communicates between Controller and Repo. |
| **Controller** | `AppointmentController.java` | **The Receptionist / API:** Listens to HTTP requests from the browser/Postman (`GET`, `POST`, `PUT`, `DELETE`) and returns JSON responses. |
| **Frontend** | `index.html` | **The Face:** A responsive UI served from `src/main/resources/static/` that uses `fetch()` to call the REST APIs. |

---

## 🔄 How to Change Domain in 3 Minutes (e.g., Movie Ticket / Bus Transport)

If the lab examiner gives you a different domain on the spot, you only need to change **4 words**:

| Field in WeCARE (Healthcare) | Movies Domain | Transport Domain | Library Domain |
| :--- | :--- | :--- | :--- |
| **patientName** | `movieTitle` | `passengerName` | `bookTitle` |
| **doctorName** | `theaterName` | `busRoute` | `authorName` |
| **problem** | `seatNumbers` | `seatNumber` | `category` |
| **appointmentDate** | `showDate` | `travelDate` | `issueDate` |
| **status** | `ticketStatus` | `bookingStatus` | `returnStatus` |

Everything else (`Repository`, `Service`, `Controller`, CRUD methods, and Frontend fetch logic) remains **identical**!
