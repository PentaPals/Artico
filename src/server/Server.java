package server;

import auth_and_menus.authenticationAndMenus;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    static authenticationAndMenus onlineShop = new authenticationAndMenus();

    public static void main(String[] args) {
        System.out.println("Server started!");

        try (ServerSocket server = new ServerSocket(1211)) {
            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("Client connected: " + server.getInetAddress());
                Thread thread = new Thread(new UserThread(clientSocket, onlineShop));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}