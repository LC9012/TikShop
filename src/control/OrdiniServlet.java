package control;

import model.Order;
import model.OrderDAO;
import model.ReviewDAO; // Assicurati di avere questo DAO nel tuo progetto

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@WebServlet("/OrdiniServlet")
public class OrdiniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?returnUrl=OrdiniServlet");
            return;
        }

     
        //Creiamo istanze dei DAO che ci servono.
        OrderDAO orderDAO = new OrderDAO();
        ReviewDAO reviewDAO = new ReviewDAO();

        try {
            //Usiamo i metodi sulle istanze create
            List<Order> orders = orderDAO.getOrdersByUser(userId);

            LocalDate oggi = LocalDate.now();
            for (Order o : orders) {
                // Aggiorniamo lo stato
                if (!"Annullato".equalsIgnoreCase(o.getStatus()) && !"Restituito".equalsIgnoreCase(o.getStatus())) {
                    java.util.Date orderDate = o.getOrderDate();
                    if (orderDate != null) {
                        LocalDate orderLocalDate = orderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        long days = ChronoUnit.DAYS.between(orderLocalDate, oggi);
                        
                        String nuovoStato = o.getStatus();
                        if (days < 1) {
                            nuovoStato = "In preparazione";
                        } else if (days < 3) {
                            nuovoStato = "In consegna";
                        } else {
                            nuovoStato = "Consegnato";
                        }
                        o.setStatus(nuovoStato);
                    }
                }
            }

            //Logica per sapere quali prodotti sono stati recensiti
            Map<Integer, Boolean> userReviewedMap = reviewDAO.getReviewedProductsMapForUser(userId);

            // Passiamo i dati alla pagina JSP
            req.setAttribute("orders", orders);
            req.setAttribute("userReviewed", userReviewedMap);
            
            req.getRequestDispatcher("/ordini.jsp").forward(req, resp);

        } catch (SQLException e) {
            e.printStackTrace();
            // In caso di errore del database inoltra a una pagina di errore o mostra un messaggio
            throw new ServletException("Errore del database durante il recupero dei dati.", e);
        }
    }
}