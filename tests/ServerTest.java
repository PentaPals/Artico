package tests;

import cart.CartItem;
import client.Client;
import database.DatabaseManager;
import discount.NewDiscount;
import inventory.InventoryItem;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import product.Product;
import server.*;

import java.io.*;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServerTest {
    @Test
    @Order(1)
    public void testServer() throws RuntimeException {
        // Start server by running Server main method
        Thread t = new Thread(() -> Server.main(null));
        t.start();

        // Wait 1 second to make sure server has started before connecting
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    public void testDatabaseManager() {
        DatabaseManager testDatabaseManager = new DatabaseManager();
        System.out.println(testDatabaseManager);
    }

    @Test
    @Order(3)
    public void testProduct() {
        // Testing new product
        Product testProduct = new Product(1, "Протеиново барче", 10, 200, "Фитнес", 10);
        DatabaseManager testDatabaseManager = new DatabaseManager();

        testDatabaseManager.addProduct(testProduct, 2);
        Product output = testDatabaseManager.getProductById(1);
        assertEquals(testProduct.getId(), output.getId());
        assertEquals(testProduct.getName(), output.getName());
        assertEquals(testProduct.getPrice(), output.getPrice());
        assertEquals(testProduct.getMin_price(), output.getMin_price());
        assertEquals(testProduct.getProductDescription(), output.getProductDescription());
        assertEquals(testProduct.getDiscountPercentage(), output.getDiscountedPrice());
    }
    //НЕ МОЖЕ ДА СЕ ТЕСТВА КЛИНТСКАТА ЧАСТ
    @Test
    @Order(4)
    public void testClient() throws IOException, InterruptedException { // throws BDSMException
        String[] testArgs = null;
        int data = 9;
        String inputData = String.valueOf(data) + "\n"; // Append newline as the input may be read line by line
        ByteArrayInputStream testIn = new ByteArrayInputStream(inputData.getBytes());

        // Redirecting System.in to our ByteArrayInputStream
        System.setIn(testIn);

        // Now when your Client.main() method is invoked, it will read from our ByteArrayInputStream
//        Client.main(testArgs);

        // Optionally, add a delay if necessary before further assertions
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @Order(5)
    public void testInventoryItem() {
        InventoryItem testInventoryItem = new InventoryItem(1, "", 12.4, 100);
        System.out.println(testInventoryItem);
    }

    @Test
    @Order(6)
    public void testCartItem() {
        Product testProduct = new Product(1, "Bunny", 10, 200, "Velikden", 10);
        CartItem testCartItem = new CartItem(testProduct, 1);
        System.out.println(testCartItem);
    }

    @Test
    @Order(7)
    public void testNewDiscount() {
        Timestamp testTimestamp = new Timestamp(2024, 12, 12, 1, 1, 1, 1);
        NewDiscount testNewDiscount = new NewDiscount(1, "Velikden", 4, testTimestamp, 5);
        System.out.println(testNewDiscount);
    }


    @Test
    @Order(8)
    public void testCheckUsername_valid() {
        String input = "username\n";
        Scanner scanner = new Scanner(new StringReader(input));
        assertEquals("username", Client.checkUsername(scanner));
    }

    @Test()
    @Order(9)
    public void testCheckFirstName_valid() {
        String input = "John\n";
        Scanner scanner = new Scanner(new StringReader(input));
        assertEquals("John", Client.checkFirstName(scanner));
    }

    @Test
    @Order(10)
    public void testCheckLastName_valid() {
        String input = "Doe\n";
        Scanner scanner = new Scanner(new StringReader(input));
        assertEquals("Doe", Client.checkLastName(scanner));
    }

    @Test
    @Order(11)
    public void testCheckEmail_valid() {
        String input = "email@example.com\n";
        Scanner scanner = new Scanner(new StringReader(input));
        assertEquals("email@example.com", Client.checkEmail(scanner));
    }


    @Test
    @Order(12)
    void testConstructorAndGetters() {
        // Създаване на ново намаление
        int id = 1;
        String name = "Spring Sale";
        double percentage = 10.0;
        Timestamp startDate = new Timestamp(System.currentTimeMillis());
        int durationDays = 7;

        NewDiscount discount = new NewDiscount(id, name, percentage, startDate, durationDays);

        // Проверка на правилността на конструктора и на методите за вземане на стойности
        assertEquals(id, discount.getID());
        assertEquals(name, discount.getName());
        assertEquals(percentage, discount.getPercentage());
        assertEquals(startDate, discount.getStart_date());
        assertEquals(durationDays, discount.getDuration_days());
    }

    @Test
    @Order(13)
    void testSetters() {
        // Създаване на ново намаление
        NewDiscount discount = new NewDiscount(1, "Summer Sale", 15.0, new Timestamp(System.currentTimeMillis()), 10);

        // Промяна на стойности чрез сетъри
        int newId = 2;
        String newName = "Autumn Sale";
        double newPercentage = 20.0;
        Timestamp newStartDate = new Timestamp(System.currentTimeMillis() + 1000); // 1 секунда по-късно от сега
        int newDurationDays = 14;

        discount.setID(newId);
        discount.setName(newName);
        discount.setPercentage((int) newPercentage);
        discount.setStart_date(newStartDate);
        discount.setDuration_days(newDurationDays);

        // Проверка на правилността на сетърите
        assertEquals(newId, discount.getID());
        assertEquals(newName, discount.getName());
        assertEquals(newPercentage, discount.getPercentage());
        assertEquals(newStartDate, discount.getStart_date());
        assertEquals(newDurationDays, discount.getDuration_days());
        }

    @Test
    @Order(14)
    void testEqualsAndName() {
        // Създаване на две различни намаления със същите стойности
        NewDiscount discount1 = new NewDiscount(1, "Spring Sale", 10.0, new Timestamp(0), 7);
        NewDiscount discount2 = new NewDiscount(1, "Spring Sale", 10.0, new Timestamp(0), 7);

        // Проверка дали методите equals и name работят коректно
        assertEquals(discount1.getName(), discount2.getName());
    }

    @Test
    @Order(15)
    void testNotEquals() {
        // Създаване на две различни намаления с различни стойности
        NewDiscount discount1 = new NewDiscount(1, "Spring Sale", 10.0, new Timestamp(0), 7);
        NewDiscount discount2 = new NewDiscount(2, "Summer Sale", 15.0, new Timestamp(1000), 10);

        // Проверка дали методът equals връща false за различни обекти
        assertFalse(discount1.equals(discount2));
    }




}

