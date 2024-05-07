package users;

import product.Product;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {

    private String firstName;
    private String lastName;
    private String addressLine;
    private String email;
    private double balance;

    public Customer(String username, String password, String firstName, String lastName,String email, String addressLine,  double balance) {
        super(username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.addressLine = addressLine;
        this.balance = balance;
    }

    @Override
    public UserType getUserType() {
        return UserType.CUSTOMER;
    }
    public double getBalance() {
        return balance;
    }
    public double setBalance(double balance) {
        this.balance = balance;
        return balance;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddressLine() {
        return addressLine;
    }


    public String getEmail() {
        return email;
    }
}

