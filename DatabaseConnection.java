// DatabaseConnection.java
import java.sql.*;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/healthcare_management";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ Database connected successfully!");
                
                // Test query
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                if (rs.next()) {
                    System.out.println("📊 Total users in database: " + rs.getInt(1));
                }
                rs.close();
                stmt.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "❌ Database Connection Failed!\n\nError: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Test if we can execute updates
    public static boolean testUpdate() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            int result = stmt.executeUpdate("UPDATE users SET name=name WHERE user_id='U001'");
            stmt.close();
            System.out.println("✅ Update test successful! Rows affected: " + result);
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Update test failed: " + e.getMessage());
            return false;
        }
    }
}