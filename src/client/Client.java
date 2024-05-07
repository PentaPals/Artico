package client;

import database.DatabaseManager;
import users.Customer;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static auth_and_menus.authenticationAndMenus.*;

public class Client {
    static DatabaseManager databaseManager = new DatabaseManager();
    private static boolean createCustomerUser(Scanner scanner) {
        String customerUsername = checkUsername(scanner);
        String customerPassword = checkPassword(scanner);
        String customerFirstName = checkFirstName(scanner);
        String customerLastName = checkLastName(scanner);
        System.out.println("Address Line, Country , City , PostalCode");
        String customerAddressLine = scanner.nextLine();
        String customerEmail = checkEmail(scanner);
        double customerBalance = 0;
        Customer customer = new Customer(customerUsername, customerPassword, customerFirstName, customerLastName, customerEmail,customerAddressLine,  customerBalance);
        return databaseManager.registerCustomer(customer);
    }

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", 1211)) {
            System.out.println("Client connected: " + socket.getInetAddress());
            Scanner scanner = new Scanner(System.in);
            int option;


                System.out.println("===========================================");
                System.out.println("|            Welcome to Artico!           |");
                System.out.println("===========================================\n");
                System.out.println("-------------------------------------------");
                System.out.println("|         Please select an option:        |");
                System.out.println("|     1. Sign in                          |");
                System.out.println("|     2. Sign up                          |");
                System.out.println("|     9. Exit                             |");
                System.out.println("-------------------------------------------\n");
                System.out.println("Option: ");

                option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        System.out.println("Enter username:");
                        String username = scanner.nextLine();
                        System.out.println("Enter password:");
                        String password = scanner.nextLine();
                        PrintStream output = new PrintStream(socket.getOutputStream());
                        output.println(username + " " + password);
                        break;
                    case 2:
                        if(createCustomerUser(scanner)) {
                            System.out.println("Customer Registered Successfully!");
                        } else {
                            System.out.println("Customer Registration Failed! Please try Again");
                        }
                        break;
                    case 9:
                        System.out.println("Exiting...");
                        System.out.println("Exited!");
                        break;
                    default:
                        System.out.println("Invalid option. Please select a valid option.");
                }

            PrintStream output = new PrintStream(socket.getOutputStream());
            startResponseReaderThread(socket);
            while (true) {
                String command = scanner.nextLine();
                if (command.equals("exit")) {
                    break;
                }
                output.println(command);
            }
        }
    }

    private static void startResponseReaderThread(Socket socket) {
        Thread responseThread = new Thread(() -> {
            try {
                Scanner serverResponse = new Scanner(socket.getInputStream());
                while (serverResponse.hasNextLine()) {
                    String response = serverResponse.nextLine();
                    System.out.println(response);
                }
            } catch (IOException e) {
                System.out.println("Server disconnected");
            }
        });
        responseThread.start();
    }
    public static String checkUsername(Scanner scanner) {
        String username;
        do {
            System.out.println("Please enter username:");
            username = scanner.nextLine();
            if (databaseManager.usernameExists(username)) {
                System.out.println("Username already exists! Please choose a different username.");
            }
        } while(databaseManager.usernameExists(username));

        return username;
    }
    public static String checkPassword(Scanner scanner) {
        String password;
        do {
            System.out.println("Please enter password:");
            password = scanner.nextLine();
            if (!isValidPassword(password)) {
                System.out.println("Password must be at least 8 characters long and contain at least one lowercase letter, one uppercase letter, one special character and one digit! Please try again.");
            }
        } while(!isValidPassword(password));

        return password;
    }

    public static String checkFirstName(Scanner scanner) {
        String fname;
        do {
            System.out.println("First Name: ");
            fname = scanner.nextLine();
            if (!isOnlyLetters(fname)) {
                System.out.println("First Name must contain only letters! Please try again.");
            }
        } while(!isOnlyLetters(fname));

        return fname;
    }

    public static String checkLastName(Scanner scanner) {
        String lname;
        do {
            System.out.println("Last Name: ");
            lname = scanner.nextLine();
            if (!isOnlyLetters(lname)) {
                System.out.println("Last Name must contain only letters! Please try again.");
            }
        } while(!isOnlyLetters(lname));

        return lname;
    }

    public static String checkEmail(Scanner scanner) {
        String email;
        do {
            System.out.println("Email: ");
            email = scanner.nextLine();
            if (!isValidEmail(email)) {
                System.out.println("Invalid email! Please try again.");
            }
        } while(!isValidEmail(email));

        return email;
    }
}
