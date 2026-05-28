package control;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/SubmitReviewServlet")

public class SubmitReviewServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = (Integer) req.getSession().getAttribute("userId");
        String productIdStr = req.getParameter("productId");
        String ratingStr = req.getParameter("rating");
        String comment = req.getParameter("comment");
        resp.setContentType("application/json");

        if (userId == null || productIdStr == null || ratingStr == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Dati mancanti\"}");
            return;
        }

        int productId = Integer.parseInt(productIdStr);
        int rating = Integer.parseInt(ratingStr);

        try (Connection conn = util.DBUtil.getConnection()) {
            // Verifica che l'utente abbia acquistato il prodotto e che l'ordine sia consegnato
        	String checkSql = "SELECT COUNT(*) FROM order_items oi " +
                    "JOIN orders o ON oi.order_id = o.id " +
                    "WHERE o.user_id=? AND oi.product_id=? AND o.status='Completato'";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, userId);
                checkPs.setInt(2, productId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        resp.getWriter().write("{\"success\":false,\"message\":\"Non puoi recensire questo prodotto\"}");
                        return;
                    }
                }
            }

            // Inserisci la recensione
            String sql = "INSERT INTO reviews (user_id, product_id, comment, rating) VALUES (?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE comment=VALUES(comment), rating=VALUES(rating), date=CURRENT_TIMESTAMP";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setInt(2, productId);
                ps.setString(3, comment);
                ps.setInt(4, rating);
                ps.executeUpdate();
            }

            resp.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"success\":false,\"message\":\"Errore server\"}");
        }
    }
}