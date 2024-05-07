package database;

import discount.NewDiscount;
import inventory.InventoryItem;
import orders.monthlyOrders;
import product.Product;
import users.Administrator;
import users.Customer;
import users.Staff;
import users.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/articodbmysql";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "8888";

    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean usernameExists(String username) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/articodbmysql", "root", "8888");
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void addProduct(Product product, int quantity) {
        try {
            PreparedStatement productStatement = connection.prepareStatement("INSERT INTO products (name, price, min_price, category, quantity) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            productStatement.setString(1, product.getName());
            productStatement.setDouble(2, product.getPrice());
            productStatement.setDouble(3, product.getMin_price());
            productStatement.setString(4, product.getProductDescription());
            productStatement.setInt(5, quantity);
            productStatement.executeUpdate();
            ResultSet generatedKeys = productStatement.getGeneratedKeys();
            int productId = 0;
            if (generatedKeys.next()) {
                productId = generatedKeys.getInt(1);
            }
            productStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<InventoryItem> getInventory() {
        List<InventoryItem> inventory = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT p.id, p.name, p.price, p.quantity  FROM products p");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int productId = resultSet.getInt("id");
                String productName = resultSet.getString("name");
                double productPrice = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                inventory.add(new InventoryItem(productId, productName,productPrice, quantity));
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventory;
    }

    public int getProductPromotion(int productID) {
        int promotionId=-1;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT product_id,promotion_id  FROM product_promotions");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if(productID == resultSet.getInt("product_id")){
                    promotionId = resultSet.getInt("promotion_id");
                }
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promotionId;
    }

    public static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            String hashedPassword = sb.toString();

            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean registerCustomer(Customer customer) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (role, username, password, first_name, last_name, email, address, balance) VALUES (3, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, customer.getUsername());
            statement.setString(2, encryptPassword(customer.getPassword()));
            statement.setString(3, customer.getFirstName());
            statement.setString(4, customer.getLastName());
            statement.setString(5, customer.getEmail());
            statement.setString(6, customer.getAddressLine());
            statement.setDouble(7, customer.getBalance());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerStaff(Staff staff) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (role,username, password, first_name, last_name, email) VALUES (2 , ?, ?, ?, ?, ?)");
            statement.setString(1, staff.getUsername());
            statement.setString(2, encryptPassword(staff.getPassword()));
            statement.setString(3, staff.getFirst_name());
            statement.setString(4, staff.getLast_name());
            statement.setString(5, staff.getEmail());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean createDiscount(NewDiscount newDiscount) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO promotions (id , name , percentage, start_date, duration_days) VALUES (?,?,?,?,?)");
            statement.setInt(1, newDiscount.getID());
            statement.setString(2, newDiscount.getName());
            statement.setDouble(3,newDiscount.getPercentage());
            statement.setTimestamp(4,newDiscount.getStart_date());
            statement.setInt(5,newDiscount.getDuration_days());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int checkForPromotionsProduct(int inventoryID) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM product_promotions WHERE product_id=?");
            statement.setInt(1, inventoryID);
            ResultSet resultSet = statement.executeQuery();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryID;
    }

    public boolean addToOrders(Customer customer,double total) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO orders (user_firstName,user_lastName, total) VALUES (?, ?,?)");
            statement.setString(1, customer.getFirstName());
            statement.setString(2,customer.getLastName());
            statement.setDouble(3,total);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<monthlyOrders> getOrders(){
        List<monthlyOrders> orders = new ArrayList<>();
        double total=0;
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM orders");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                int ordersId =resultSet.getInt("id");
                total =resultSet.getDouble("total");
                orders.add(new monthlyOrders(ordersId,total));
            }
            statement.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean insertDiscount(InventoryItem inventoryItem, NewDiscount newDiscount) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO product_promotions (product_id, promotion_id) VALUES (?, ?)");
            statement.setInt(1, inventoryItem.getProductId());
            statement.setInt(2, newDiscount.getID());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<NewDiscount> getDiscountItems() {
        List<NewDiscount> discounts = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * From promotions");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int promotionId= resultSet.getInt("id");
                String promotionName = resultSet.getString("name");
                int promotionPercentage = resultSet.getInt("percentage");
                Timestamp promotionStartDate = resultSet.getTimestamp("start_date");
                int promotionDurationDays= resultSet.getInt("duration_days");
                discounts.add(new NewDiscount(promotionId,promotionName,promotionPercentage,promotionStartDate,promotionDurationDays));
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discounts;
        }

    public boolean updateCustomerBalance(Customer customer) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET balance = ? WHERE username = ?");
            statement.setDouble(1, customer.getBalance());
            statement.setString(2, customer.getUsername());
            int rowsUpdated = statement.executeUpdate();
            statement.close();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateInventoryItem(InventoryItem item) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "UPDATE products SET quantity = ? WHERE id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, item.getQuantity());
            statement.setInt(2, item.getProductId());

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public InventoryItem getInventoryItemByProductId(int productId) {
        List<InventoryItem> inventory = getInventory();
        for (InventoryItem item : inventory) {
            if (item.getProductId() == productId) {
                return item;
            }
        }
        return null;
    }

    public NewDiscount getDiscountTypeByDiscountID(int discountID) {
        List<NewDiscount> newDiscounts =getDiscountItems();
        for (NewDiscount newDiscount : newDiscounts) {
            if (newDiscount.getID() == discountID) {
                return newDiscount;
            }
        }
        return null;
    }


    public boolean updateInventoryQuantity(int productId, int newQuantity) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE products SET quantity = ? WHERE id = ?");
            statement.setInt(1, newQuantity);
            statement.setInt(2, productId);
            int rowsUpdated = statement.executeUpdate();
            statement.close();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public Product getProductById(int productId) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM products WHERE id = ?");
            statement.setInt(1, productId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getDouble("min_price"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateProduct(Product product) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE products SET name = ?, price = ?, min_price = ?, category = ? WHERE id = ?");
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setDouble(3, product.getMin_price());
            statement.setString(4, product.getProductDescription());
            statement.setInt(5, product.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProductPromotion(InventoryItem inventoryItem) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM product_promotions WHERE product_id = ? ");
            statement.setInt(1, inventoryItem.getProductId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(InventoryItem inventoryItem) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM products WHERE id = ? ");
            statement.setInt(1, inventoryItem.getProductId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Customer showCustomerDetailsByUsername(String username) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Customer(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        resultSet.getString("address"),
                        resultSet.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM products");
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getDouble("min_price"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity")
                );
                products.add(product);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public User getUser(String username, String password) {
        try {
            PreparedStatement adminStatement = connection.prepareStatement("SELECT * FROM users WHERE role = 1 AND username = ? AND password = ?");
            adminStatement.setString(1, username);
            adminStatement.setString(2, password);
            ResultSet adminResultSet = adminStatement.executeQuery();

            if (adminResultSet.next()) {
                return new Administrator(
                        adminResultSet.getString("username"),
                        adminResultSet.getString("password")
                );
            }

            PreparedStatement staffStatement = connection.prepareStatement("SELECT * FROM users WHERE role = 2 AND username = ? AND password = ?");
            staffStatement.setString(1, username);
            staffStatement.setString(2, password);
            ResultSet staffResultSet = staffStatement.executeQuery();

            if (staffResultSet.next()) {
                return new Staff(
                        staffResultSet.getString("username"),
                        staffResultSet.getString("password"),
                        staffResultSet.getString("first_name"),
                        staffResultSet.getString("last_name"),
                        staffResultSet.getString("email")
                );
            }
            PreparedStatement customerStatement = connection.prepareStatement("SELECT * FROM users WHERE role = 3 AND username = ? AND password = ?");
            customerStatement.setString(1, username);
            customerStatement.setString(2, password);
            ResultSet customerResultSet = customerStatement.executeQuery();

            if (customerResultSet.next()) {
                return new Customer(
                        customerResultSet.getString("username"),
                        customerResultSet.getString("password"),
                        customerResultSet.getString("first_name"),
                        customerResultSet.getString("last_name"),
                        customerResultSet.getString("email"),
                        customerResultSet.getString("address"),
                        customerResultSet.getDouble("balance")
                );
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }




    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
