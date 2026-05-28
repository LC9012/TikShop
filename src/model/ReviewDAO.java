package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap; // <-- IMPORT AGGIUNTO
import java.util.List;
import java.util.Map;     // <-- IMPORT AGGIUNTO
import util.DBUtil;

public class ReviewDAO {

   
    public List<Review> getReviewsByProductId(int productId, String ratingFilter) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder("SELECT r.id, r.comment, r.rating, r.date, u.name as user_name ")
                                          .append("FROM reviews r JOIN users u ON r.user_id = u.id ")
                                          .append("WHERE r.product_id = ?");

        if (ratingFilter != null && !ratingFilter.equalsIgnoreCase("ALL")) {
            sql.append(" AND r.rating = ?");
        }
        sql.append(" ORDER BY r.date DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            ps.setInt(1, productId);
            if (ratingFilter != null && !ratingFilter.equalsIgnoreCase("ALL")) {
                ps.setInt(2, Integer.parseInt(ratingFilter));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review();
                    review.setId(rs.getInt("id"));
                    review.setUserName(rs.getString("user_name"));
                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    review.setReviewDate(rs.getTimestamp("date"));
                    reviews.add(review);
                }
            }
        } catch (NumberFormatException e) {
        }
        return reviews;
    }

    //Aggiunge una recensione al db
    public boolean addReview(int userId, int productId, int rating, String comment) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO reviews (user_id, product_id, rating, comment) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            // Controlla se esiste già una recensione
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, userId);
                psCheck.setInt(2, productId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }

            // Inserisce la nuova recensione
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setInt(1, userId);
                psInsert.setInt(2, productId);
                psInsert.setInt(3, rating);
                psInsert.setString(4, comment);
                
                int rowsAffected = psInsert.executeUpdate();
                return rowsAffected > 0;
            }
        }
    }


    //Recupera una mappa di tutti i prodotti che un utente ha recensito.

    public Map<Integer, Boolean> getReviewedProductsMapForUser(int userId) throws SQLException {
        Map<Integer, Boolean> reviewedMap = new HashMap<>();
        String sql = "SELECT product_id FROM reviews WHERE user_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviewedMap.put(rs.getInt("product_id"), true);
                }
            }
        }
        return reviewedMap;
    }
}