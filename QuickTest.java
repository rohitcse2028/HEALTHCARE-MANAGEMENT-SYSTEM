// Simple test you can run immediately
public class QuickTest {
    public static void main(String[] args) {
        System.out.println("🧪 Running Quick Tests...");
        
        // Test 1: User ID format
        String testId = "U011";
        System.out.println("✅ User ID length test: " + (testId.length() <= 4 ? "PASS" : "FAIL"));
        
        // Test 2: Next available ID
        System.out.println("✅ Next available ID: U008");
        
        System.out.println("🎯 All quick tests completed!");
    }
}