package control;

import model.OrderDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ResoOrdineServlet")
public class ResoOrdineServlet extends HttpServlet {

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

            
            if (OrderDAO.isOrderOwnedBy(orderId, userId)) {
                OrderDAO.updateOrderStatus(orderId, "restituito");
                resp.sendRedirect("OrdiniServlet?msg=Richiesta di reso avviata con successo.");
            } else {
                resp.sendRedirect("OrdiniServlet?error=Accesso non autorizzato.");
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect("OrdiniServlet?error=ID ordine non valido.");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("OrdiniServlet?error=Si è verificato un errore durante la richiesta di reso.");
        }
    }
}