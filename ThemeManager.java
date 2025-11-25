// ThemeManager.java
import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class ThemeManager {
    private static final String THEME_PREF = "dark_theme";
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    
    private static boolean isDarkMode = false;
    
    // Dark theme colors
    public static final Color DARK_BG = new Color(45, 45, 48);
    public static final Color DARK_PANEL = new Color(63, 63, 70);
    public static final Color DARK_TEXT = new Color(241, 241, 241);
    public static final Color DARK_BORDER = new Color(85, 85, 85);
    public static final Color DARK_BUTTON = new Color(0, 122, 204);
    public static final Color DARK_BUTTON_HOVER = new Color(28, 151, 234);
    
    // Light theme colors
    public static final Color LIGHT_BG = new Color(240, 240, 240);
    public static final Color LIGHT_PANEL = Color.WHITE;
    public static final Color LIGHT_TEXT = Color.BLACK;
    public static final Color LIGHT_BORDER = new Color(200, 200, 200);
    public static final Color LIGHT_BUTTON = new Color(0, 102, 204);
    public static final Color LIGHT_BUTTON_HOVER = new Color(0, 78, 156);
    
    public static void initialize() {
        isDarkMode = prefs.getBoolean(THEME_PREF, false);
    }
    
    public static boolean isDarkMode() {
        return isDarkMode;
    }
    
    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
        prefs.putBoolean(THEME_PREF, isDarkMode);
    }
    
    public static void applyTheme(Component component) {
        if (isDarkMode) {
            applyDarkTheme(component);
        } else {
            applyLightTheme(component);
        }
    }
    
    private static void applyDarkTheme(Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            panel.setBackground(DARK_BG);
            panel.setForeground(DARK_TEXT);
        }
        else if (component instanceof JFrame) {
            JFrame frame = (JFrame) component;
            frame.getContentPane().setBackground(DARK_BG);
        }
        else if (component instanceof JDialog) {
            JDialog dialog = (JDialog) component;
            dialog.getContentPane().setBackground(DARK_BG);
        }
        else if (component instanceof JButton) {
            JButton button = (JButton) component;
            button.setBackground(DARK_BUTTON);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
            
            // Hover effect
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(DARK_BUTTON_HOVER);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(DARK_BUTTON);
                }
            });
        }
        else if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            label.setForeground(DARK_TEXT);
        }
        else if (component instanceof JTextField) {
            JTextField textField = (JTextField) component;
            textField.setBackground(DARK_PANEL);
            textField.setForeground(DARK_TEXT);
            textField.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
            textField.setCaretColor(DARK_TEXT);
        }
        else if (component instanceof JPasswordField) {
            JPasswordField passwordField = (JPasswordField) component;
            passwordField.setBackground(DARK_PANEL);
            passwordField.setForeground(DARK_TEXT);
            passwordField.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
            passwordField.setCaretColor(DARK_TEXT);
        }
        else if (component instanceof JTextArea) {
            JTextArea textArea = (JTextArea) component;
            textArea.setBackground(DARK_PANEL);
            textArea.setForeground(DARK_TEXT);
            textArea.setCaretColor(DARK_TEXT);
        }
        else if (component instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) component;
            combo.setBackground(DARK_PANEL);
            combo.setForeground(DARK_TEXT);
        }
        else if (component instanceof JTable) {
            JTable table = (JTable) component;
            table.setBackground(DARK_PANEL);
            table.setForeground(DARK_TEXT);
            table.setGridColor(DARK_BORDER);
            table.getTableHeader().setBackground(DARK_BUTTON);
            table.getTableHeader().setForeground(Color.WHITE);
        }
        else if (component instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) component;
            tabbedPane.setBackground(DARK_BG);
            tabbedPane.setForeground(DARK_TEXT);
        }
        else if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            scrollPane.getViewport().setBackground(DARK_PANEL);
            scrollPane.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
        }
        else if (component instanceof JMenuBar) {
            JMenuBar menuBar = (JMenuBar) component;
            menuBar.setBackground(DARK_PANEL);
            menuBar.setForeground(DARK_TEXT);
        }
    }
    
    private static void applyLightTheme(Component component) {
        // Reset to default light theme
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            panel.setBackground(LIGHT_BG);
            panel.setForeground(LIGHT_TEXT);
        }
        else if (component instanceof JFrame) {
            JFrame frame = (JFrame) component;
            frame.getContentPane().setBackground(LIGHT_BG);
        }
        else if (component instanceof JButton) {
            JButton button = (JButton) component;
            button.setBackground(LIGHT_BUTTON);
            button.setForeground(Color.WHITE);
            button.setBorder(UIManager.getBorder("Button.border"));
            
            // Remove hover effects
            for (var listener : button.getMouseListeners()) {
                if (listener.toString().contains("MouseAdapter")) {
                    button.removeMouseListener(listener);
                }
            }
        }
        else if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            label.setForeground(LIGHT_TEXT);
        }
        else if (component instanceof JTextField) {
            JTextField textField = (JTextField) component;
            textField.setBackground(Color.WHITE);
            textField.setForeground(LIGHT_TEXT);
            textField.setBorder(UIManager.getBorder("TextField.border"));
            textField.setCaretColor(LIGHT_TEXT);
        }
        // ... similar reset for other components
    }
    
    public static void applyThemeToContainer(Container container) {
        applyTheme(container);
        for (Component comp : container.getComponents()) {
            applyTheme(comp);
            if (comp instanceof Container) {
                applyThemeToContainer((Container) comp);
            }
        }
    }
}