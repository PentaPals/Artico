package auth_and_menus;//

import cart.CartItem;
import database.DatabaseManager;
import discount.NewDiscount;
import inventory.InventoryItem;
import orders.monthlyOrders;
import product.Product;
import users.Administrator;
import users.Customer;
import users.Staff;
import users.User;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class authenticationAndMenus {
    private static DatabaseManager databaseManager;
    private static Map<String, List<CartItem>> userCarts;
    private static PrintStream printOutStream;
    private static final String LETTER_REGEX = "^[a-zA-Z]+$";
    private static final String EMAIL_REGEX = "\\b[A-Za-z0-9._-]+@[A-Za-z.-]+\\.[A-Za-z]{2,}\\b";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!?*_~.-])[A-Za-z\\d@#$%^&+=!?*_~.-]{8,20}$";
    private static final String POSTCODE_REGEX = "^[a-zA-Z0-9]+$";
    private static final String DOUBLE_REGEX = "^\\d+(\\.\\d{1,2})?$";
    private static final String QUANTITY_REGEX = "[0-9]+";

    public authenticationAndMenus() {
        databaseManager = new DatabaseManager();
        userCarts = new HashMap();
    }



    public void loginUser(String username, String password, Socket clientSocket) throws IOException {
        printOutStream = new PrintStream(clientSocket.getOutputStream());
        if(username.equals("admin1")&& password.equals("admin1")){
            User user = databaseManager.getUser(username,password);
            printOutStream.println("Logged in successfully!");
            switch (user.getUserType())
            {
                case ADMINISTRATOR:
                    Administrator administrator = (Administrator) user;
                    this.adminMenu(administrator,clientSocket);
            }
        }else {
            User user = databaseManager.getUser(username, DatabaseManager.encryptPassword(password));
            if (user != null ) {
                printOutStream.println("Logged in successfully!");
                switch (user.getUserType()) {
                    case CUSTOMER:
                        Customer customer = (Customer)user;
                        this.customerMenu(customer, clientSocket);
                        break;
                    case STAFF:
                        Staff staff = (Staff)user;
                        this.staffMenu(staff, clientSocket);
                        break;
                    case ADMINISTRATOR:
                        Administrator admin = (Administrator)user;
                        this.adminMenu(admin, clientSocket);
                }
            } else {
                printOutStream.println("Invalid username or password!");
                printOutStream.println("Please try again!");
            }
        }
    }




    public void adminMenu(Administrator admin, Socket clientSocket) throws IOException {
        printOutStream.println("Welcome " + admin.getUsername() + "!");
        Scanner scanner = new Scanner(clientSocket.getInputStream());
        boolean createAnotherStaff = true;
        while(createAnotherStaff){
            printOutStream.println("Creating new Staff member...");
            if (this.createStaffUser(scanner)) {
                printOutStream.println("Staff registered successfully!");
            } else {
                printOutStream.println("Staff registration failed! Please try again!");
            }

            printOutStream.println("Do you want to create another Staff member? (yes/no)");
            String response = scanner.nextLine().trim().toLowerCase();
            if(!response.equals("yes"))
            {
                createAnotherStaff=false;
            }
        }
        printOutStream.println("Thank you and goodbye, " + admin.getUsername() + "!");



    }


    public static boolean isOnlyLetters(String str) {
        return str.matches(LETTER_REGEX);
    }

    public static boolean isValidPassword(String password) {
        return password.matches(PASSWORD_REGEX);
    }

    public static boolean isValidEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }

    public static boolean isValidPostCode(String postCode) {
        return postCode.matches(POSTCODE_REGEX);
    }

    public static boolean isValidDouble(double price) {
        String priceString = Double.toString(price);
        return priceString.matches(DOUBLE_REGEX);
    }

    public static boolean isValidQuantity(int quantity) {
        String quantityString = Integer.toString(quantity);
        return quantityString.matches(QUANTITY_REGEX);
    }

    public static String checkUsername(Scanner scanner) {
        String username;
        do {
            printOutStream.println("Please enter username:");
            username = scanner.nextLine();
            if (databaseManager.usernameExists(username)) {
                printOutStream.println("Username already exists! Please choose a different username.");
            }
        } while(databaseManager.usernameExists(username));

        return username;
    }

    public static String checkPassword(Scanner scanner) {
        String password;
        do {
            printOutStream.println("Please enter password:");
            password = scanner.nextLine();
            if (!isValidPassword(password)) {
                printOutStream.println("Password must be at least 8 characters long and contain at least one lowercase letter, one uppercase letter, one special character and one digit! Please try again.");
            }
        } while(!isValidPassword(password));

        return password;
    }

    public static String checkFirstName(Scanner scanner) {
        String fname;
        do {
            printOutStream.println("First Name: ");
            fname = scanner.nextLine();
            if (!isOnlyLetters(fname)) {
                printOutStream.println("First Name must contain only letters! Please try again.");
            }
        } while(!isOnlyLetters(fname));

        return fname;
    }

    public static String checkLastName(Scanner scanner) {
        String lname;
        do {
            printOutStream.println("Last Name: ");
            lname = scanner.nextLine();
            if (!isOnlyLetters(lname)) {
                printOutStream.println("Last Name must contain only letters! Please try again.");
            }
        } while(!isOnlyLetters(lname));

        return lname;
    }

    public static String checkEmail(Scanner scanner) {
        String email;
        do {
            printOutStream.println("Email: ");
            email = scanner.nextLine();
            if (!isValidEmail(email)) {
                printOutStream.println("Invalid email! Please try again.");
            }
        } while(!isValidEmail(email));

        return email;
    }

    public static double checkBalance(Scanner scanner) {
        double balance;
        do {
            printOutStream.println("Balance: ");
            if (scanner.hasNextDouble()) {
                balance = scanner.nextDouble();
                scanner.nextLine();
                if (!isValidDouble(balance)) {
                    printOutStream.println("Invalid input for balance! Please try again.");
                }
            } else {
                printOutStream.println("Invalid input for price! Please enter a valid number.");
                scanner.nextLine();
                balance = -1.0;
            }
        } while(!isValidDouble(balance));

        return balance;
    }

    public static int checkPriceInt(Scanner scanner) {
        int price;
        do {

            if (scanner.hasNextDouble()) {
                price = scanner.nextInt();
                scanner.nextLine();
                if (!isValidDouble(price)) {
                    printOutStream.println("Invalid input for price! Please try again.");
                }
            } else {
                printOutStream.println("Invalid input for price! Please enter a valid number.");
                scanner.nextLine();
                price = -1;
            }
        } while(!isValidDouble(price));

        return price;
    }
    public static double checkPrice(Scanner scanner) {
        double price;
        do {

            if (scanner.hasNextDouble()) {
                price = scanner.nextDouble();
                scanner.nextLine();
                if (!isValidDouble(price)) {
                    printOutStream.println("Invalid input for price! Please try again.");
                }
            } else {
                printOutStream.println("Invalid input for price! Please enter a valid number.");
                scanner.nextLine();
                price = -1.0;
            }
        } while(!isValidDouble(price));
        return price;
    }

    public static double checkPercentage(Scanner scanner) {
        double percent;
        do {
            if (scanner.hasNextDouble()) {
                percent = scanner.nextDouble();
                scanner.nextLine();
                if (!isValidDouble(percent)) {
                    printOutStream.println("Invalid input for percentage! Please try again.");
                }
            } else {
                printOutStream.println("Invalid input for percentage! Please enter a valid number.");
                scanner.nextLine();
                percent = -1.0;
            }
        } while(!isValidDouble(percent));

        return percent;
    }

    public static int checkQuantity(Scanner scanner) {
        int quantity;
        do {
            printOutStream.println("Enter product quantity: ");
            if (scanner.hasNextInt()) {
                quantity = scanner.nextInt();
                scanner.nextLine();
                if (quantity <= 0) {
                    printOutStream.println("Quantity must be a positive number! Please try again.");
                    quantity = -1;
                } else if (!isValidQuantity(quantity)) {
                    printOutStream.println("Invalid input for quantity! Please enter a valid number.");
                    quantity = -1;
                }
            } else {
                printOutStream.println("Invalid input for quantity! Please enter a valid number.");
                scanner.nextLine();
                quantity = -1;
            }
        } while(quantity <= 0 || !isValidQuantity(quantity));

        return quantity;
    }

    private boolean createStaffUser(Scanner scanner) {
        String staffUsername = checkUsername(scanner);
        String staffPassword = checkPassword(scanner);
        printOutStream.println("First Name:");
        String staffFirstName = scanner.nextLine();
        printOutStream.println("Last Name:");
        String staffLastName= scanner.nextLine();
        String staffEmail = checkEmail(scanner);
        Staff staff = new Staff(staffUsername, staffPassword, staffFirstName,staffLastName, staffEmail);
        return databaseManager.registerStaff(staff);
    }

    private void staffMenu(Staff staff, Socket clientSocket) throws IOException {
        printOutStream.println("===========================================");
        printOutStream.println("|     Welcome " + staff.getLast_name()+ " " +staff.getLast_name() + " to Artico!     |");
        printOutStream.println("===========================================\n");
        Scanner scanner = new Scanner(clientSocket.getInputStream());
        boolean continueOperations = true;
            while(continueOperations) {
                printOutStream.println("-------------------------------------------");
                printOutStream.println("|         Please select an option:        |");
                printOutStream.println("|     1. Add new product to inventory     |");
                printOutStream.println("|     2. Remove product from inventory    |");
                printOutStream.println("|     3. View all products                |");
                printOutStream.println("|     4. Check Inventory                  |");
                printOutStream.println("|     5. Update Product Details           |");
                printOutStream.println("|     6. Update Inventory Details         |");
                printOutStream.println("|     7. Add Promotion Type               |");
                printOutStream.println("|     8. Add Item/s to Promotion Even     |");
                printOutStream.println("|     9. Check monthly report             |");
                printOutStream.println("|     0. Logout                           |");
                printOutStream.println("-------------------------------------------");
                printOutStream.println("Your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        this.addProductToInventory(scanner);
                        break;
                    case 2:
                        this.removeProductFromInventory(scanner);
                        break;
                    case 3:
                        printOutStream.println("All products:");
                        this.viewAllProducts();
                        break;
                    case 4:
                        printOutStream.println("Inventory:");
                        List<InventoryItem> inventory = this.getInventory();
                        for(InventoryItem item:inventory){
                            printOutStream.println("ID: " + item.getProductId() + ", Name: " + item.getProductName() + ", Quantity: " + item.getQuantity());
                        }
                        break;
                    case 5:
                        printOutStream.println("Enter product ID to update: ");
                        int id = scanner.nextInt();
                        this.updateProductMenu(id, scanner);
                        break;
                    case 6:
                        printOutStream.println("Enter product ID to update: ");
                        int prodId = scanner.nextInt();
                        int prodQuantity = checkQuantity(scanner);
                        this.updateInventoryItemQuantity(prodId, prodQuantity);
                        break;
                    case 7:
                        printOutStream.println("Create New Promotion Type.");
                        printOutStream.println("Promotion Name: ");
                        String promotionName = scanner.nextLine();
                        printOutStream.println("Discount Percent: ");
                        double discountPercentage = checkPercentage(scanner);
                        printOutStream.println("Promotion Start date in yyyy-MM-dd HH:mm:ss");
                        String  promotionStartDate = scanner.nextLine();
                        Timestamp timestamp2 = parseStringToTimestamp(promotionStartDate);
                        printOutStream.println("Enter Duration Days: ");
                        int promotionDurationDays = checkPriceInt(scanner);
                        NewDiscount newDiscount = new NewDiscount(0,promotionName,discountPercentage,timestamp2,promotionDurationDays);
                        databaseManager.createDiscount(newDiscount);
                        break;
                    case 8:
                        printOutStream.println("Enter Product/s for Promotion Event:");
                        int productsToAdd = scanner.nextInt();
                        int count= 0;
                        while (productsToAdd > count)
                        {
                            printOutStream.println("Enter Product ID: ");
                            int promotionProduct= scanner.nextInt();
                            printOutStream.println("Enter Discount Type ID: ");
                            int discountType = scanner.nextInt();
                            databaseManager.insertDiscount(databaseManager.getInventoryItemByProductId(promotionProduct),databaseManager.getDiscountTypeByDiscountID(discountType));
                            printOutStream.println("Discount has been added to product!!");
                            count ++;
                        }
                        break;
                    case 9:
                        double monthlyReport = 0;
                        monthlyReport(monthlyReport);
                    break;
                    case 0:
                        printOutStream.println("Are you sure you want to Log out?");
                        printOutStream.println("1 - YES / 2 - NO");
                        int logout=scanner.nextInt();
                        if(logout==1)
                        {
                            continueOperations =false;
                            printOutStream.println("Logging out...");
                            printOutStream.println("Logged out!");
                        }
                        break;
                    default:
                        printOutStream.println("Invalid choice. Please try again.");
                }
            }
    }

    public void updateProductMenu(int productId, Scanner scanner) {
        Product productToUpdate = databaseManager.getProductById(productId);
        if (productToUpdate != null) {
            printOutStream.println("Product found. Enter new details for the product:");
            scanner.nextLine();
            printOutStream.println("Enter new name (or press Enter to keep the same): ");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                productToUpdate.setName(newName);
            }
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            printOutStream.println("Enter new Minimal Price: ");
            double minPrice = checkPrice(scanner);
            df.format(productToUpdate.setMin_price(minPrice));

            printOutStream.println("Enter new Price: ");
            double newPrice = checkPrice(scanner);
            while (newPrice <= minPrice)
            {
                printOutStream.println("Enter new Price: ");
                newPrice=checkPrice(scanner);
            }
            df.format(productToUpdate.setPrice(newPrice));
            printOutStream.println("Enter new description (or press Enter to keep the same): ");
            String newDescription = scanner.nextLine();
            if (!newDescription.isEmpty()) {
                productToUpdate.setProductDescription(newDescription);
            }
            databaseManager.updateProduct(productToUpdate);
            printOutStream.println("Product updated successfully!");
        } else {
            printOutStream.println("Product with ID " + productId + " not found.");
        }
    }

    public List<InventoryItem> getInventory() {
        return databaseManager.getInventory();
    }

    private void addProductToInventory(Scanner scanner) {
        printOutStream.println("Enter product name:");
        String productName = scanner.nextLine();
        printOutStream.println("Enter product minimal price:");
        double min_price = checkPrice(scanner);
        printOutStream.println("Enter Regular Price:");
        double productPrice = checkPrice(scanner);
        while ( productPrice<= min_price)
        {
            printOutStream.println("Enter Regular Price:");
            productPrice=checkPrice(scanner);
        }
        int productQuantity = checkQuantity(scanner);
        printOutStream.println("Enter category");
        String category = scanner.nextLine();

        Product newProduct = new Product(0, productName, productPrice, min_price, category, productQuantity);
        databaseManager.addProduct(newProduct, productQuantity);
        printOutStream.println("Product added to inventory successfully!");
    }

    public void updateInventoryItemQuantity(int productId, int newQuantity) {
        InventoryItem item = databaseManager.getInventoryItemByProductId(productId);
        if (item != null) {
            item.setQuantity(newQuantity);
            boolean updateSuccess = databaseManager.updateInventoryItem(item);
            if (updateSuccess) {
                printOutStream.println("Inventory item quantity updated successfully.");
            } else {
                printOutStream.println("Failed to update inventory item quantity.");
            }
        } else {
            System.out.println("Inventory item not found.");
        }
    }

    public boolean productExists(int productId) {
        Product product = databaseManager.getProductById(productId);
        return product != null;
    }

    private void removeProductFromInventory(Scanner scanner) {
        printOutStream.println("Enter product ID to remove:");
        try {
            int productIdToRemove = scanner.nextInt();
            if (databaseManager.getInventoryItemByProductId(productIdToRemove)!= null) {
                databaseManager.deleteProductPromotion(databaseManager.getInventoryItemByProductId(productIdToRemove));
                databaseManager.deleteProduct(databaseManager.getInventoryItemByProductId(productIdToRemove));
                printOutStream.println("Product removed from inventory successfully!");
            } else {
                printOutStream.println("Product with ID " + productIdToRemove + " not found.");
            }
        } catch (NumberFormatException e) {
            printOutStream.println("Invalid input! Please enter a valid product ID.");
        }
    }

    private void monthlyReport(double monthlyReport) {
        List<monthlyOrders> orders = databaseManager.getOrders();
        if (orders.isEmpty()) {
            printOutStream.println("No monthly report available, no purchases for this month!");
        } else {
            for (monthlyOrders morders : orders) {
                monthlyReport += morders.getTotal();
            }
            printOutStream.printf("Monthly Report is: $%.2f%n", monthlyReport);
        }
    }

    private void viewAllProducts() {
        List<Product> products = databaseManager.getAllProducts();
        List<NewDiscount> newDiscounts = databaseManager.getDiscountItems();
        if (products.isEmpty()) {
            printOutStream.println("No products available in inventory.");
        } else {
            for (Product product : products)
            {
                Iterator iterator = newDiscounts.iterator();
                iterator.hasNext();
                NewDiscount newDiscount = (NewDiscount)iterator.next();
                printOutStream.println("ID: " + product.getId());
                printOutStream.println("Name: " + product.getName());
                printOutStream.println("Price: $" + product.getPrice());
                printOutStream.println("Description: " + product.getProductDescription());
                if (product.getProductDescription() != null) {
                    product.setDiscountPercentage(newDiscount.getPercentage());
                    printOutStream.println("Discount: " + product.getDiscountPercentage() + "% off");
                } else {
                    printOutStream.println("No discount applied");
                }
                printOutStream.println("------------------------------------------------");
            }
        }
    }

    private void customerMenu(Customer customer, Socket clientSocket) throws IOException {
        printOutStream.println("=========================================================");
        printOutStream.println("Welcome " + customer.getFirstName() + " " + customer.getLastName() + " to Artico!");
        printOutStream.println("=========================================================\n");
        Scanner scanner = new Scanner(clientSocket.getInputStream());
        boolean continueShopping = true;

        while(continueShopping) {
            printOutStream.println("-------------------------------------------");
            printOutStream.println("|         Please select an option:         |");
            printOutStream.println("|       1. View products                   |");
            printOutStream.println("|       2. View Promotions                 |");
            printOutStream.println("|       3. Add product to cart             |");
            printOutStream.println("|       4. Remove product from cart        |");
            printOutStream.println("|       5. View cart                       |");
            printOutStream.println("|       6. Checkout                        |");
            printOutStream.println("|       7. View profile                    |");
            printOutStream.println("|       8. Add Funds                       |");
            printOutStream.println("|       9. Logout                          |");
            printOutStream.println("-------------------------------------------\n");
            printOutStream.println("Your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    printOutStream.println("Products:");
                    this.viewAllProductsForCustomer();
                    break;
                case 2:
                    printOutStream.println("Products that have Discount:");
                    this.viewAllDiscountedProducts();
                    break;
                case 3:
                    printOutStream.println("Enter product ID to add to cart:");
                    int productId = scanner.nextInt();
                    int quantity = checkQuantity(scanner);
                    this.addToCart(customer, productId, quantity);
                    break;
                case 4:
                    printOutStream.println("Enter product ID to remove from cart:");
                    int productIdToRemove = scanner.nextInt();
                    this.removeFromCart(customer, productIdToRemove);
                    break;
                case 5:
                    printOutStream.println("Cart:");
                    this.viewCart(customer);
                    break;
                case 6:
                    printOutStream.println("Checkout:");
                    this.checkout(customer, printOutStream);
                    break;
                case 7:
                    String username = customer.getUsername();
                    this.viewProfile(username);
                    break;
                case 8:
                    printOutStream.println("Enter Amount of Money to Add");
                    double moneyToAdd = checkBalance(scanner);
                    double customerBalance = customer.getBalance()+moneyToAdd;
                    customer.setBalance(customerBalance);
                    databaseManager.updateCustomerBalance(customer);
                    break;
                case 9:
                    printOutStream.println("Are you sure you want to Log out?");
                    printOutStream.println("1 - YES / 2 - NO");
                    int logout=scanner.nextInt();
                    if(logout==1)
                    {
                        continueShopping =false;
                        printOutStream.println("Logging out...");
                        printOutStream.println("Logged out!");
                    }
                    break;
                default:
                    printOutStream.println("Invalid choice. Please try again.");
            }
        }
    }



    public Customer viewProfile(String username) {
        Customer customer = databaseManager.showCustomerDetailsByUsername(username);
        if (customer != null) {
            printOutStream.println("Customer Profile:");
            printOutStream.println("Username: " + customer.getUsername());
            printOutStream.println("First Name: " + customer.getFirstName());
            printOutStream.println("Last Name: " + customer.getLastName());
            printOutStream.println("Address, Country, City, Postalcode: " + customer.getAddressLine());
            printOutStream.println("Email: " + customer.getEmail());
            printOutStream.println("Balance: $" + customer.getBalance());
        } else {
            printOutStream.println("Customer with username '" + username + "' not found.");
        }
        return customer;
    }
    private void viewAllProductsForCustomer() {
        List<Product> products = databaseManager.getAllProducts();
        List<NewDiscount> discounts = databaseManager.getDiscountItems();

        if (products.isEmpty()) {
            printOutStream.println("No products available.");
        } else {
            for (Product product : products) {
                double price = product.getPrice();
                Iterator<NewDiscount> iterator = discounts.iterator();
                if (iterator.hasNext()) {
                    NewDiscount newDiscount = iterator.next();
                    printOutStream.println("" + product.getId() + ". " + product.getName() +
                            " - Regular Price: $" + price +
                            String.format(" - Price After Discount: %.2f$ ", (price / 100) * newDiscount.getPercentage()) +
                            " - Discount Percentage: %" + newDiscount.getPercentage());
                } else {
                    printOutStream.println("" + product.getId() + ". " + product.getName() +
                            " - Regular Price: $" + price +
                            " - No discount available");
                }
            }
        }
    }

    private void viewAllDiscountedProducts() {
        List<NewDiscount> discounts = databaseManager.getDiscountItems();
        List<InventoryItem> inventoryItems = databaseManager.getInventory();
        if (discounts == null) {
            printOutStream.println("No promotions available.");
        } else {
            for(NewDiscount discount:discounts)
            {
                Iterator inventory = inventoryItems.iterator();
                inventory.hasNext();
                InventoryItem inventoryItem= (InventoryItem) inventory.next();
                if (discount.getName() != null) {
                    printOutStream.println("Product name: " + inventoryItem.getProductName() + ".  Promotion Type: " + discount.getName() + ", Start Date of the Promotion: " + discount.getStart_date() + " Days Left of Promotion: " + discount.getDuration_days());
                } else {

                    printOutStream.println("" + inventoryItem.getProductName() + ".  No promotion Type! , Start Date of the Promotion: " + discount.getStart_date() + " Days Left of Promotion: " + discount.getDuration_days());
                }
            }
        }
    }

    private void removeFromCart(Customer customer, int productId) {
        Product productToRemove = databaseManager.getProductById(productId);
        if (productToRemove != null) {
            List<CartItem> cart = userCarts.getOrDefault(customer.getUsername(), new ArrayList<>());
            // Find and remove the CartItem containing the productToRemove
            cart.removeIf(cartItem -> cartItem.getProduct().getId() == productId);
            userCarts.put(customer.getUsername(), cart);
            printOutStream.println("Product removed from cart: " + productToRemove.getName());
        } else {
            printOutStream.println("Product not found.");
        }
    }

    private void addToCart(Customer customer, int productId, int quantity) {
        Product productToAdd = databaseManager.getProductById(productId);
        if (productToAdd != null) {
            List<CartItem> cart = userCarts.getOrDefault(customer.getUsername(), new ArrayList());
            cart.add(new CartItem(productToAdd, quantity));
            userCarts.put(customer.getUsername(), cart);
            printOutStream.println("Product added to cart: " + productToAdd.getName());
        } else {
            printOutStream.println("Product not found.");
        }
    }

    private void viewCart(Customer customer) {
        List<CartItem> cart = userCarts.getOrDefault(customer.getUsername(), new ArrayList());
        List<NewDiscount> newDiscounts =databaseManager.getDiscountItems();
        Iterator iterator = newDiscounts.iterator();
        iterator.hasNext();
        NewDiscount newDiscount = (NewDiscount)iterator.next();
        if (cart.isEmpty()) {
            printOutStream.println("Your cart is empty.");
        } else {
            printOutStream.println("Cart items:");
            for(CartItem cartItem : cart)
            {
                printOutStream.println(cartItem.getProduct().getName() + " - Quantity: " + cartItem.getQuantity() + ", Price: $" + cartItem.getProduct().getPrice() + ", Price after Discount: $"+ (cartItem.getProduct().getPrice()/100)*newDiscount.getPercentage() + " Discount Percentage: %" + newDiscount.getPercentage());
            }
        }
    }

    private void checkout(Customer customer, PrintStream printOutStream) {
        List<CartItem> cart = userCarts.getOrDefault(customer.getUsername(), new ArrayList());
        List<NewDiscount> newDiscounts = databaseManager.getDiscountItems();
        double totalPrice = 0.0;
        boolean itemsUnavailable = false;
        for (CartItem cartItem : cart)
        {
            Iterator iterator = newDiscounts.iterator();
            iterator.hasNext();
            NewDiscount newDiscount = (NewDiscount)iterator.next();
            Product product = cartItem.getProduct();
            int cartQuantity = cartItem.getQuantity();
            InventoryItem inventoryItem = databaseManager.getInventoryItemByProductId(product.getId());
            int invQuantity = inventoryItem.getQuantity();
            if (inventoryItem != null && invQuantity >= cartQuantity) {
                if (databaseManager.checkForPromotionsProduct(product.getId())==cartItem.getProduct().getId() || databaseManager.checkForPromotionsProduct(product.getId())== databaseManager.getProductPromotion(product.getId())) {
                    totalPrice += ((product.getPrice() * (double) cartQuantity)/100) *newDiscount.getPercentage();
                    databaseManager.updateInventoryQuantity(product.getId(),inventoryItem.setQuantity(invQuantity-cartQuantity));
                    databaseManager.addToOrders(customer,totalPrice);
                }
                else {
                    totalPrice += product.getPrice() * (double)cartQuantity;
                    databaseManager.updateInventoryQuantity(product.getId(), inventoryItem.setQuantity(invQuantity - cartQuantity));
                    databaseManager.addToOrders(customer,totalPrice);
                }
                } else {
                itemsUnavailable = true;
                printOutStream.println("Product not available: " + product.getName());
            }
        }
            if (!itemsUnavailable) {
                boolean checkoutSuccess = this.performCheckoutProcess(customer, totalPrice);
                if (checkoutSuccess) {
                    printOutStream.println("Checkout successful. Total: $" + String.format("%.2f", totalPrice));
                    userCarts.remove(customer.getUsername());
                } else {
                    printOutStream.println("Checkout failed. Insufficient balance or other issues.");
                }
            } else {
                printOutStream.println("Checkout failed. Some products are not available.");
            }
    }

    private boolean performCheckoutProcess(Customer customer, double totalPrice) {
        double remainingBalance = customer.getBalance() - totalPrice;
        if (remainingBalance >= 0.0) {
            customer.setBalance(remainingBalance);
            if (databaseManager.updateCustomerBalance(customer)) {
                return true;
            } else {
                customer.setBalance(customer.getBalance() + totalPrice);
                return false;
            }
        } else {
            return false;
        }
    }

    public static Timestamp parseStringToTimestamp(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date parsedDate = dateFormat.parse(dateString);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}