// PDFReportService.java - FINAL FIXED VERSION
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFReportService {
    
    public void generateAppointmentReport(String reportType) {
        try {
            String fileName = "";
            String title = "";
            
            switch (reportType) {
                case "daily":
                    fileName = "Daily_Appointments_Report_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf";
                    title = "Daily Appointments Report - " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                    break;
                case "monthly":
                    fileName = "Monthly_Appointments_Report_" + new SimpleDateFormat("yyyyMM").format(new Date()) + ".pdf";
                    title = "Monthly Appointments Report - " + new SimpleDateFormat("MMMM yyyy").format(new Date());
                    break;
                case "all":
                    fileName = "All_Appointments_Report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
                    title = "All Appointments Report";
                    break;
            }
            
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Header section
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Healthcare Management System");
            contentStream.endText();
            
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 720);
            contentStream.showText(title);
            contentStream.endText();
            
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("Generated on: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            contentStream.endText();
            
            // Statistics section
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 670);
            contentStream.showText("Summary Statistics:");
            contentStream.endText();
            
            Connection conn = DatabaseConnection.getConnection();
            String countQuery = "";
            
            switch (reportType) {
                case "daily":
                    countQuery = "SELECT COUNT(*) as total, " +
                                "SUM(CASE WHEN status = 'Scheduled' THEN 1 ELSE 0 END) as scheduled, " +
                                "SUM(CASE WHEN status = 'Confirmed' THEN 1 ELSE 0 END) as confirmed, " +
                                "SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) as completed " +
                                "FROM appointments WHERE appointment_date = CURDATE()";
                    break;
                case "monthly":
                    countQuery = "SELECT COUNT(*) as total, " +
                                "SUM(CASE WHEN status = 'Scheduled' THEN 1 ELSE 0 END) as scheduled, " +
                                "SUM(CASE WHEN status = 'Confirmed' THEN 1 ELSE 0 END) as confirmed, " +
                                "SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) as completed " +
                                "FROM appointments WHERE MONTH(appointment_date) = MONTH(CURDATE()) AND YEAR(appointment_date) = YEAR(CURDATE())";
                    break;
                case "all":
                    countQuery = "SELECT COUNT(*) as total, " +
                                "SUM(CASE WHEN status = 'Scheduled' THEN 1 ELSE 0 END) as scheduled, " +
                                "SUM(CASE WHEN status = 'Confirmed' THEN 1 ELSE 0 END) as confirmed, " +
                                "SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) as completed " +
                                "FROM appointments";
                    break;
            }
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(countQuery);
            
            if (rs.next()) {
                int yPos = 650;
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                
                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPos);
                contentStream.showText("• Total Appointments: " + rs.getInt("total"));
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPos - 15);
                contentStream.showText("• Scheduled: " + rs.getInt("scheduled"));
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPos - 30);
                contentStream.showText("• Confirmed: " + rs.getInt("confirmed"));
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPos - 45);
                contentStream.showText("• Completed: " + rs.getInt("completed"));
                contentStream.endText();
            }
            rs.close();
            stmt.close();
            
            // Appointments list
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 580);
            contentStream.showText("Appointments Details:");
            contentStream.endText();
            
            // Table header
            int tableY = 560;
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 9);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, tableY);
            contentStream.showText("ID");
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.newLineAtOffset(80, tableY);
            contentStream.showText("Patient");
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.newLineAtOffset(180, tableY);
            contentStream.showText("Doctor");
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.newLineAtOffset(280, tableY);
            contentStream.showText("Date");
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.newLineAtOffset(350, tableY);
            contentStream.showText("Time");
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.newLineAtOffset(400, tableY);
            contentStream.showText("Status");
            contentStream.endText();
            
            // CORRECTED QUERIES - USING USERS TABLE FOR NAMES
            String dataQuery = "";
            switch (reportType) {
                case "daily":
                    dataQuery = "SELECT a.appointment_id, " +
                               "u_p.name as patient_name, " +
                               "u_d.name as doctor_name, " +
                               "a.appointment_date, " +
                               "a.appointment_time, " +
                               "a.status " +
                               "FROM appointments a " +
                               "JOIN patients p ON a.patient_id = p.patient_id " +
                               "JOIN users u_p ON p.user_id = u_p.user_id " +
                               "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                               "JOIN users u_d ON d.user_id = u_d.user_id " +
                               "WHERE a.appointment_date = CURDATE() " +
                               "ORDER BY a.appointment_time";
                    break;
                case "monthly":
                    dataQuery = "SELECT a.appointment_id, " +
                               "u_p.name as patient_name, " +
                               "u_d.name as doctor_name, " +
                               "a.appointment_date, " +
                               "a.appointment_time, " +
                               "a.status " +
                               "FROM appointments a " +
                               "JOIN patients p ON a.patient_id = p.patient_id " +
                               "JOIN users u_p ON p.user_id = u_p.user_id " +
                               "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                               "JOIN users u_d ON d.user_id = u_d.user_id " +
                               "WHERE MONTH(a.appointment_date) = MONTH(CURDATE()) AND YEAR(a.appointment_date) = YEAR(CURDATE()) " +
                               "ORDER BY a.appointment_date, a.appointment_time";
                    break;
                case "all":
                    dataQuery = "SELECT a.appointment_id, " +
                               "u_p.name as patient_name, " +
                               "u_d.name as doctor_name, " +
                               "a.appointment_date, " +
                               "a.appointment_time, " +
                               "a.status " +
                               "FROM appointments a " +
                               "JOIN patients p ON a.patient_id = p.patient_id " +
                               "JOIN users u_p ON p.user_id = u_p.user_id " +
                               "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                               "JOIN users u_d ON d.user_id = u_d.user_id " +
                               "ORDER BY a.appointment_date DESC, a.appointment_time";
                    break;
            }
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(dataQuery);
            
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            tableY = 545;
            int rowCount = 0;
            
            while (rs.next() && tableY > 100) {
                contentStream.beginText();
                contentStream.newLineAtOffset(50, tableY);
                contentStream.showText(rs.getString("appointment_id"));
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(80, tableY);
                contentStream.showText(shortenText(rs.getString("patient_name"), 15));
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(180, tableY);
                contentStream.showText(shortenText(rs.getString("doctor_name"), 15));
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(280, tableY);
                contentStream.showText(rs.getString("appointment_date"));
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(350, tableY);
                contentStream.showText(rs.getString("appointment_time"));
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(400, tableY);
                contentStream.showText(rs.getString("status"));
                contentStream.endText();
                
                tableY -= 12;
                rowCount++;
            }
            
            if (rs.next()) {
                contentStream.beginText();
                contentStream.newLineAtOffset(50, tableY);
                contentStream.showText("... and " + (getRemainingCount(conn, reportType) - rowCount) + " more records");
                contentStream.endText();
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            contentStream.close();
            
            // File save karein
            String downloadsPath = System.getProperty("user.home") + "/Downloads/";
            File file = new File(downloadsPath + fileName);
            document.save(file);
            document.close();
            
            // Success message
            JOptionPane.showMessageDialog(null, 
                "PDF Report Generated Successfully!\n\n" +
                "File: " + fileName + "\n" +
                "Location: " + downloadsPath, 
                "Report Generated", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // File open karein
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error generating PDF report: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private String shortenText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    private int getRemainingCount(Connection conn, String reportType) throws SQLException {
        String countQuery = "";
        switch (reportType) {
            case "daily":
                countQuery = "SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()";
                break;
            case "monthly":
                countQuery = "SELECT COUNT(*) FROM appointments WHERE MONTH(appointment_date) = MONTH(CURDATE()) AND YEAR(appointment_date) = YEAR(CURDATE())";
                break;
            case "all":
                countQuery = "SELECT COUNT(*) FROM appointments";
                break;
        }
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(countQuery);
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        stmt.close();
        return count;
    }
}