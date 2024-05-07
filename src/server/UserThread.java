package server;

import auth_and_menus.authenticationAndMenus;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class UserThread implements Runnable {

    authenticationAndMenus onlineShop;
    private final Socket clientSocket;

    public UserThread(Socket clientSocket, authenticationAndMenus onlineShop) {
        this.clientSocket = clientSocket;
        this.onlineShop = onlineShop;
    }



    @Override
    public void run() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(clientSocket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String credentials = scanner.nextLine();
        System.out.println("Credentials received, can start processing");
        String userName = credentials.split(" ")[0];
        String password = credentials.split(" ")[1];

        try {
            onlineShop.loginUser(userName, password, clientSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}