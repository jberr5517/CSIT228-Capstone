# Attendance Management System

## Group Members
- Jose Daniel Y. Adlawan
- Jober Oscar F. Guartel
- Rainer V. Aguirre
- Lee Christian A. Cueva
- Vinz Johan C. Enad

## Project Description
The Attendance Management System is a desktop application designed to digitize and streamline the process of tracking attendance for organizations such as schools, offices, or events. It replaces manual paper-based logs or error-prone spreadsheets with a centralized, role-based digital solution. The system addresses common problems like lost records, time theft, difficulty in searching historical data, and lack of access control. 

## Proposed Features
- User login / authentication
- Add / edit / delete attendance records
- Time-in / time-out logging
- Display data in tables (JavaFX TableView)
- Search and filtering
- Database integration (e.g., SQLite/MySQL)
- Role-based access (admin/user)
- Data validation

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
