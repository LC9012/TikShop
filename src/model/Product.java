package model;
import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String videoUrl;
    private String fotoUrl;
    private double price;
    private String description;
    private int rating;
    private String department;
    private boolean isActive;

    public Product() {}

    // GETTERS e SETTERS
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}