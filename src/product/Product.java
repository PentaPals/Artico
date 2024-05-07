package product;

public class Product {
    private int id;
    private String name;
    private double price;
    private double min_price;
    private String productDescription;
    private boolean hasDiscount;
    private double discountPercentage;

    public Product(int id, String name, double price, double min_price, String productDescription, double discountPercentage) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.productDescription = productDescription;
        this.discountPercentage = discountPercentage;
        this.min_price= min_price;
    }


    public double getMin_price() {
        return min_price;
    }

    public double setMin_price(double min_price) {
        this.min_price = min_price;
        return min_price;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public boolean getHasDiscount() {
        return hasDiscount;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public double getDiscountedPrice() {
        if (hasDiscount) {
            return price * (1 - discountPercentage / 100);
        } else {
            return price;
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double setPrice(double price) {
        this.price = price;
        return price;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setHasDiscount(boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}
