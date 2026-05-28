package control;

import util.DBUtil;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
@WebServlet("/DeleteAccountServlet")

public class DeleteAccountServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            resp.sendRedirect("login.jsp?returnUrl=impostazioni.jsp");
            return;
        }
        try (Connection conn = DBUtil.getConnection()) {
            // Elimina tutte le recensioni dell'utente
            try (PreparedStatement delReviews = conn.prepareStatement("DELETE FROM reviews WHERE user_id=?")) {
                delReviews.setInt(1, userId);
                delReviews.executeUpdate();
            }
            // Elimina tutti gli ordini dell'utente
            try (PreparedStatement delOrders = conn.prepareStatement("DELETE FROM orders WHERE user_id=?")) {
                delOrders.setInt(1, userId);
                delOrders.executeUpdate();
            }
            // Elimina il carrello dell'utente
            try (PreparedStatement delCart = conn.prepareStatement("DELETE FROM cart WHERE user_id=?")) {
                delCart.setInt(1, userId);
                delCart.executeUpdate();
            }
            // Elimina l'utente
            try (PreparedStatement delUser = conn.prepareStatement("DELETE FROM users WHERE id=?")) {
                delUser.setInt(1, userId);
                delUser.executeUpdate();
            }
            // Invalida la sessione
            session.invalidate();
            resp.sendRedirect("register.jsp?msg=account_eliminato");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("impostazioni.jsp?error=1");
        }
    }
}