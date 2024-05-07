package orders;

public class monthlyOrders {
    private int ordersId;
    private double total;

    public monthlyOrders(int ordersId, double total) {
        this.ordersId = ordersId;
        this.total = total;
    }

    public int getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(int ordersId) {
        this.ordersId = ordersId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
