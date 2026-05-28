package model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int productId;
    private String name;
    private double price;
    private int quantity;
    private String fotoUrl;

    public CartItem(int productId, String name, double price, int quantity, String fotoUrl) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.fotoUrl = fotoUrl;
    }

    public CartItem() {}

    // Getters e Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    // Metodo per calcolare il totale del singolo item
    public double getTotal() {
        return price * quantity;
    }
}