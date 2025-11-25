// AnalyticsChartsPanel.java
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AnalyticsChartsPanel extends JPanel {  // ✅ NAME CHANGE
    private JTabbedPane chartTabs;

    public AnalyticsChartsPanel() {  // ✅ CONSTRUCTOR NAME CHANGE
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        chartTabs = new JTabbedPane();
        chartTabs.addTab("📊 Appointments Chart", createAppointmentChart());
        chartTabs.addTab("🥧 Users Distribution", createUserPieChart());
        
        add(chartTabs, BorderLayout.CENTER);
    }

    private JPanel createAppointmentChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Appointment status count
            String query = "SELECT status, COUNT(*) as count FROM appointments GROUP BY status";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                dataset.addValue(rs.getInt("count"), "Appointments", rs.getString("status"));
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Fallback data
            dataset.addValue(5, "Appointments", "Scheduled");
            dataset.addValue(3, "Appointments", "Confirmed"); 
            dataset.addValue(1, "Appointments", "Completed");
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Appointments by Status",
            "Status", 
            "Number of Appointments",
            dataset
        );
        
        return new ChartPanel(chart);  // ✅ JFreeChart's ChartPanel
    }

    private JPanel createUserPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // User role distribution
            String query = "SELECT role, COUNT(*) as count FROM users WHERE status='Active' GROUP BY role";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                dataset.setValue(rs.getString("role"), rs.getInt("count"));
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Fallback data
            dataset.setValue("Admin", 1);
            dataset.setValue("Doctor", 3);
            dataset.setValue("Patient", 3);
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
            "User Distribution by Role",
            dataset,
            true, true, false
        );
        
        return new ChartPanel(chart);  // ✅ JFreeChart's ChartPanel
    }
}