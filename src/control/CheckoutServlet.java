
package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.CartDAO;
import model.CartItem; // <-- IMPORT MANCANTE, AGGIUNTO ORA

@WebServlet("/CheckoutServlet")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // L'utente deve essere loggato per accedere al checkout
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?returnUrl=checkout.jsp");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        CartDAO cartDAO = new CartDAO();
        
        try {
            List<CartItem> cartItems = cartDAO.getCartForUser(userId);

            if (cartItems.isEmpty()) {
                // Se il carrello è vuoto non permette il checkout
                response.sendRedirect(request.getContextPath() + "/CartServlet?error=empty");
                return;
            }

            double subtotal = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
            
            // Gestione Coupon e sconto
            double discountRate = (session.getAttribute("discount") != null) ? (Double) session.getAttribute("discount") : 0.0;
            double discountAmount = subtotal * discountRate;
            double total = subtotal - discountAmount;
            
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("subtotal", subtotal);
            request.setAttribute("discountAmount", discountAmount);
            request.setAttribute("total", total);

            request.getRequestDispatcher("/checkout.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Errore di database durante la preparazione del checkout.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}