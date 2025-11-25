// Main.java - COMPLETE FIXED VERSION
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel for better appearance - FIXED
        try {
            // Use cross-platform look and feel instead
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Look and feel not available, using default.");
        }
        
        // Initialize Theme Manager before creating any frames
        ThemeManager.initialize();
        
        // FIXED: Lambda expression replaced with anonymous class
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}