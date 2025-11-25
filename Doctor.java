public class Doctor extends User {

    public Doctor(String username) {
        super(username);
    }

    @Override
    public void openDashboard() {
        System.out.println("Opening Doctor Dashboard for: " + username);
        new DoctorDashboard().setVisible(true);
    }
}
