public class Admin extends User {

    public Admin(String username) {
        super(username);
    }

    @Override
    public void openDashboard() {
        System.out.println("Opening Admin Dashboard for: " + username);
        // Here you can open your AdminDashboard frame
        new AdminDashboard().setVisible(true);
    }
}
