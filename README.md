# HEALTHCARE-MANAGEMENT-SYSTEM

PROJECT TITLE: Healthcare Management System
PURPOSE: Comprehensive hospital management software with role-based access
VERSION: 1.0
DATE: 2024
TECHNOLOGY: Java Swing, MySQL, JDBC

================================================================================
SYSTEM REQUIREMENTS:
================================================================================
- Java JDK 8 or above
- Bluej/Intellij
- MySQL Server 5.7+
- MySQL JDBC Connector
- Xammp Controller
- Minimum 2GB RAM
- Windows 7/10/11 or Linux

================================================================================
INSTALLATION GUIDE:
================================================================================
STEP 1: DATABASE SETUP
----------------------
Install-The following JAR Files And load it in Java IDE.
(i) mysql-connector-j-9.4.0jar - MySQL JDBC Driver
(ii) jcommon-1.0.23.jar - JCommon Library (JFreeChart dependencies)
(iii) ifreechart-1.5.3.jar - JFreeChart Library (Charts and graphs)
(iv) pdfbox-2.0.27.jar - Apache PDFBox (PDF generation)
(v) fontbox-2.0.27.jar - Apache FontBox (PDF fonts - PDFBox dependency)
(vi) commons-logging-1.2.jar - Apache Commons Logging (Logging utility)

STEP 2: DATABASE SETUP
----------------------
1. Install XAMPP/WAMP or MySQL Server
2. Start Apache and MySQL services
3. Open phpMyAdmin (http://localhost/phpmyadmin)
4. Create new database: 'healthcare_management'
5. Import the provided SQL schema file

STEP 3: JDBC CONNECTOR SETUP
-----------------------------
1. Download mysql-connector-j-9.4.0.jar
2. Place it in project lib/ folder
3. Add to project build path

STEP 4: COMPILE AND RUN
-----------------------
# Compile all Java files:
javac -cp ".;lib/mysql-connector-j-9.4.0.jar" src/*.java

# Run application:
java -cp ".;lib/mysql-connector-j-9.4.0.jar;src" Main

================================================================================
LOGIN CREDENTIALS:
================================================================================

ADMIN ACCOUNTS:
---------------
👨‍💼 Username: admin
🔑 Password: admin123
📍 Role: Administrator
💼 Access: Full system access, user management, analytics

DOCTOR ACCOUNTS:
----------------
👨‍⚕️ Username: dramit
🔑 Password: doctor123
📍 Role: Doctor
💼 Access: Patient records, appointments, medical history

👩‍⚕️ Username: drpriya  
🔑 Password: doctor123
📍 Role: Doctor
💼 Access: Patient records, appointments, medical history

👨‍⚕️ Username: drsanjay
🔑 Password: doctor123
📍 Role: Doctor
💼 Access: Patient records, appointments, medical history

PATIENT ACCOUNTS:
-----------------
👩 Username: anjali
🔑 Password: patient123
📍 Role: Patient
💼 Access: Personal records, appointment booking

👨 Username: rohit
🔑 Password: patient123  
📍 Role: Patient
💼 Access: Personal records, appointment booking

================================================================================
FEATURES BY ROLE:
================================================================================

ADMIN FEATURES:
✅ User Management (Add/Edit/Delete users)
✅ Appointment Management
✅ System Analytics and Reports
✅ PDF Report Generation
✅ System Settings Configuration

DOCTOR FEATURES:
✅ View Patient Records
✅ Manage Appointments
✅ Update Medical History
✅ View Schedule
✅ Prescription Management

PATIENT FEATURES:
✅ Book Appointments
✅ View Medical History
✅ Update Personal Profile
✅ Cancel Appointments
✅ View Appointment History

================================================================================
DATABASE CONFIGURATION:
================================================================================

Database Name: healthcare_management
Host: localhost
Port: 3306
Username: root
Password: [Your MySQL password - blank by default]

Connection URL: jdbc:mysql://localhost:3306/healthcare_management

================================================================================
TROUBLESHOOTING:
================================================================================

ISSUE 1: Database Connection Failed
SOLUTION: Check MySQL service is running in XAMPP/WAMP

ISSUE 2: ClassNotFoundException for JDBC Driver
SOLUTION: Verify JDBC connector JAR is in build path

ISSUE 3: Access Denied for MySQL User
SOLUTION: Update password in DatabaseConnection.java

ISSUE 4: Table Doesn't Exist
SOLUTION: Import SQL schema file again in phpMyAdmin

ISSUE 5: Port 3306 Already in Use
SOLUTION: Change MySQL port or stop conflicting services

================================================================================
FILE STRUCTURE:
================================================================================

HealthcareProject/
├── src/
│   ├── Main.java                 (Application entry point)
│   ├── LoginFrame.java           (Login with theme support)
│   ├── DatabaseConnection.java   (Database connectivity)
│   ├── ThemeManager.java         (Dark/Light theme)
│   ├── AdminDashboard.java       (Admin interface)
│   ├── DoctorDashboard.java      (Doctor interface)
│   ├── PatientDashboard.java     (Patient interface)
│   ├── LostAndFoundFrame.java    (Lost & found system)
│   ├── PDFReportService.java     (PDF generation)
│   └── AnalyticsChartsPanel.java (Charts and analytics)
├── lib/
│   └── mysql-connector-j-9.4.0.jar
├── database/
│   └── healthcare_schema.sql
└── README.txt

================================================================================
DEVELOPER INFORMATION:
================================================================================

AUTHOR: Rohit Shukla
CONTACT: rohitcse2028@gmail.com
VERSION: 1.0

================================================================================
LICENSE: Nil
================================================================================

This project is for educational purposes.
Modify and distribute as needed.

================================================================================
END OF README
================================================================================
