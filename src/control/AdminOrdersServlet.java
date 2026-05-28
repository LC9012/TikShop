
package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Order;
import model.OrderDAO;

@WebServlet("/AdminOrdersServlet")
public class AdminOrdersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Recupera i parametri di ordinamento e filtro dalla richiesta
        String sortBy = request.getParameter("sort");
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "date";
        }

        String sortOrder = request.getParameter("order");
        if (sortOrder == null || sortOrder.isEmpty()) {
            sortOrder = "desc";
        }
        
        String statusFilter = request.getParameter("status");
        if (statusFilter == null) {
            statusFilter = "all";
        }

        try {
            List<Order> orderList = OrderDAO.getAllOrders(sortBy, sortOrder, statusFilter); 
            
            // Passa i parametri alla JSP per mantenere lo stato dei filtri
            request.setAttribute("orders", orderList);
            request.setAttribute("currentSort", sortBy);
            request.setAttribute("currentOrder", sortOrder);
            request.setAttribute("currentStatus", statusFilter);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/view-orders.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Errore DB nel recupero degli ordini per l'admin", e);
        }
    }
}