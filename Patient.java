public class Patient extends User {

    public Patient(String username) {
        super(username);
    }

    @Override
    public void openDashboard() {
        System.out.println("Opening Patient Dashboard for: " + username);
        new PatientDashboard().setVisible(true);
    }
}
