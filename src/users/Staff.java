package users;

public class Staff extends User {

    private String first_name;
    private String last_name;

    private String email;


    public Staff(String username, String password, String first_name, String last_name, String email) {
        super(username, password);
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
    }

    @Override
    public UserType getUserType() {
        return UserType.STAFF;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getEmail() {
        return email;
    }

    public String getLast_name() {
        return last_name;
    }
}

