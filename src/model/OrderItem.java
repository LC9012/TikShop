package model;

public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private String productName;
    private String productImg;
    private int quantity;
    private double price;

    // GETTER
    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductImg() { return productImg; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    // SETTER
    public void setId(int id) { this.id = id; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setProductImg(String productImg) { this.productImg = productImg; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
}