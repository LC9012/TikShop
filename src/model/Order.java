package model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Date orderDate;
    private double total;
    private int userId;
    private String customerName;
    private String status;
    private String shippingAddress;
  
    private String cardHolderName;
    private String encryptedCardNumber;
    private String cardLastFourDigits;
    private String cardExpiryDate;
    
    private List<OrderItem> items;

    public Order() {
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    
    // Getters e Setters per i campi della carta
    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public String getEncryptedCardNumber() { return encryptedCardNumber; }
    public void setEncryptedCardNumber(String encryptedCardNumber) { this.encryptedCardNumber = encryptedCardNumber; }
    public String getCardLastFourDigits() { return cardLastFourDigits; }
    public void setCardLastFourDigits(String cardLastFourDigits) { this.cardLastFourDigits = cardLastFourDigits; }
    public String getCardExpiryDate() { return cardExpiryDate; }
    public void setCardExpiryDate(String cardExpiryDate) { this.cardExpiryDate = cardExpiryDate; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}