# Attendance Management System

## Group Members
- Jose Daniel Y. Adlawan
- Jober Oscar F. Guartel
- Rainer V. Aguirre
- Lee Christian A. Cueva
- Vinz Johan C. Enad

## Project Description
The Attendance Management System is a desktop application designed to digitize and streamline the process of tracking attendance for organizations such as schools, offices, or events. It replaces manual paper-based logs or error-prone spreadsheets with a centralized, role-based digital solution. The system addresses common problems like lost records, time theft, difficulty in searching historical data, and lack of access control. 

## Features

### Authentication
- User **registration** with username, full name, password, and role selection (Admin / Student)
- Secure **login** with username and password validation
- Input validation with clear error messages (e.g. password length, duplicate usernames)

### Role-Based Access Control
- **Admin** — Full access: add/remove students, mark attendance, reset records
- **Student** — Read-only access: view and search attendance records only
- Role badge displayed in the dashboard header after login

### Live Attendance Dashboard
- Real-time summary panel showing:
  - **Total Students**
  - **Present** count
  - **Absent** count
  - **Attendance Rate** (percentage)
- Summary updates automatically as attendance is marked

### Today's Attendance (Admin)
- Mark selected students as **Present**, **Absent**, or **On Leave**
- **Remove** individual student records
- **Reset All** statuses in one click
- **Time-in** is automatically recorded when a student is marked present
- Live **search bar** to quickly find students by name, ID, or section

### Student View Mode
- Students see a read-only version of the attendance table
- Includes a dedicated **search bar** to filter by name or section
- Clear "View Only" banner to indicate limited permissions

### Attendance History Tab
- Browse **all past attendance records** stored in the database
- Filter by:
  - **Date range** (From / To date pickers)
  - **Status** (Present / Absent / Leave)
  - **Name or Section** (text search)
- Apply filters with a single button click

### Database Integration
- Powered by **MySQL** via JDBC
- All records (users, students, attendance logs) are persisted across sessions
- **Singleton** pattern used for the database connection manager

## Planned Technologies
- Java
- JavaFX
- JDBC
- Database (MySQL)
- Scene Builder

## Evaluation Criteria Mapping (Initial)
- OOP: The system will be structured using classes such as User, AttendanceRecord, DatabaseConnection, LoginController, AttendanceController, and role‑specific views. Inheritance, encapsulation, and polymorphism will be applied User base class with Admin and RegularUser subclasses.

- GUI: JavaFX with separate FXML files for each scene login, admin dashboard, user dashboard. And we will use JavaFX Table.

- UML: A Draft Level Use Case Diagram and a Class Diagram showing relationships between controllers, entities, and the database access layer will be included in the final documentation.

- Design Pattern: Singleton pattern will be used for the database connection manager to ensure a single shared connection instance.
