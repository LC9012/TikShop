
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.DBUtil;
import model.CartItem; // Assicurati che questo import punti al tuo bean CartItem

public class CartDAO {

	//Recupera il carrello di un utente dal database.
    public List<CartItem> getCartForUser(int userId) throws SQLException {
        List<CartItem> cart = new ArrayList<>();
        String sql = "SELECT c.product_id, p.name, p.price, c.quantity, p.foto " +
                     "FROM cart c JOIN products p ON c.product_id = p.id WHERE c.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cart.add(new CartItem(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("foto")
                    ));
                }
            }
        }
        return cart;
    }

    //Aggiunge un nuovo prodotto al carrello o aggiorna la quantità se già presente.

    public void addOrUpdateProduct(int userId, int productId, int quantity) throws SQLException {
        String checkSql = "SELECT quantity FROM cart WHERE user_id = ? AND product_id = ?";
        int currentQuantity = 0;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, productId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    currentQuantity = rs.getInt("quantity");
                }
            }

            if (currentQuantity > 0) {
                String updateSql = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, currentQuantity + quantity);
                    updateStmt.setInt(2, userId);
                    updateStmt.setInt(3, productId);
                    updateStmt.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, productId);
                    insertStmt.setInt(3, quantity);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    //Rimuove un prodotto dal carrello di un utente.

    public void removeProduct(int userId, int productId) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }

    //Aggiorna la quantità di un singolo prodotto nel carrello.

    public void updateQuantity(int userId, int productId, int newQuantity) throws SQLException {
        if (newQuantity <= 0) {
            removeProduct(userId, productId);
            return;
        }
        String sql = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, userId);
            stmt.setInt(3, productId);
            stmt.executeUpdate();
        }
    }

    //Unisce il carrello della sessione di un utente ospite con quello nel database.
    public void mergeSessionCartWithDbCart(int userId, List<CartItem> sessionCart) throws SQLException {
        if (sessionCart == null || sessionCart.isEmpty()) {
            return;
        }
        for (CartItem item : sessionCart) {
            addOrUpdateProduct(userId, item.getProductId(), item.getQuantity());
        }
    }

    //Svuota completamente il carrello di un utente dal database.
    public void clearCartForUser(int userId) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}