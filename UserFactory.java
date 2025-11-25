public class UserFactory {

    public static User getUser(String role, String username) {

        if (role == null) return new Patient(username);

        role = role.toLowerCase();

        switch (role) {
            case "admin":
                return new Admin(username);

            case "doctor":
                return new Doctor(username);

            default:
                return new Patient(username);
        }
    }
}
