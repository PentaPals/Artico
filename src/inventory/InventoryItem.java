package inventory;// InventoryItem class

public class InventoryItem {
    private int productId;

    private String productName;
    private int quantity;

    public InventoryItem(int productId, String productName,double productPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int setQuantity(int quantity) {
        this.quantity = quantity;
        return quantity;
    }
}
