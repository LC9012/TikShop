package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.DBUtil;

public class ProductDAO {

    // Mostra solo i prodotti attivi
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_active = TRUE";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        }
        return products;
    }
 
    public List<String> getAllDepartments() throws SQLException {
        List<String> departments = new ArrayList<>();
        String sql = "SELECT DISTINCT department FROM products WHERE department IS NOT NULL AND department != '' ORDER BY department";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                departments.add(rs.getString("department"));
            }
        }
        return departments;
    }

    // Mostra tutti i prodotti, admin
    public List<Product> getAllProductsForAdmin() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        }
        return products;
    }
    
    // Aggiungere un nuovo prodotto
    public void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, description, price, foto, video_url, department) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setString(4, product.getFotoUrl());
            stmt.setString(5, product.getVideoUrl());
            stmt.setString(6, product.getDepartment());
            stmt.executeUpdate();
        }
    }

    // Disattivare un prodotto
    public void deactivateProduct(int productId) throws SQLException {
        String sql = "UPDATE products SET is_active = false WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
    }

    // Riattivare un prodotto
    public void activateProduct(int productId) throws SQLException {
        String sql = "UPDATE products SET is_active = true WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
    }
    
    // Metodo helper per creare un oggetto Product da un ResultSet
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setVideoUrl(rs.getString("video_url"));
        p.setFotoUrl(rs.getString("foto"));
        p.setPrice(rs.getDouble("price"));
        p.setDescription(rs.getString("description"));
        p.setRating(rs.getObject("rating") != null ? rs.getInt("rating") : 0);
        p.setDepartment(rs.getString("department"));
        p.setActive(rs.getBoolean("is_active"));
        return p;
    }
    
    // Metodo per il carrello
    public CartItem getProductByIdForCart(int productId) throws SQLException {
        String sql = "SELECT name, price, foto FROM products WHERE id = ? AND is_active = TRUE";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CartItem(productId, rs.getString("name"), rs.getDouble("price"), 0, rs.getString("foto"));
                }
            }
        }
        return null;
    }
    
    public void updateProductPrice(int productId, double newPrice) throws SQLException {
        String sql = "UPDATE products SET price = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newPrice);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }
    
    public List<Product> searchProductsByName(String query) throws SQLException {
        List<Product> products = new ArrayList<>();
        // Cerca prodotti attivi che contengono la stringa di ricerca nel nome
        String sql = "SELECT id, name, foto FROM products WHERE name LIKE ? AND is_active = TRUE LIMIT 5";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + query + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setFotoUrl(rs.getString("foto"));
                    products.add(p);
                }
            }
        }
        return products;
    }
    
    
}