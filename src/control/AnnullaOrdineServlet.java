package control;

import model.OrderDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AnnullaOrdineServlet")
public class AnnullaOrdineServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;

        if (userId == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));

            
            // Verifica che l'ordine appartenga all'utente loggato prima di modificarlo
            if (OrderDAO.isOrderOwnedBy(orderId, userId)) {
                // Aggiorna lo stato dell'ordine
                OrderDAO.updateOrderStatus(orderId, "annullato");
                resp.sendRedirect("OrdiniServlet?msg=Ordine annullato con successo.");
            } else {
                //Se l'utente sta cercando di modificare un ordine non suo viene bloccato
                resp.sendRedirect("OrdiniServlet?error=Accesso non autorizzato.");
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect("OrdiniServlet?error=ID ordine non valido.");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("OrdiniServlet?error=Si è verificato un errore durante l'annullamento.");
        }
    }
}