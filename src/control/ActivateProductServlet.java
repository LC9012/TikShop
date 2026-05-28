
package control;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ProductDAO;

@WebServlet("/ActivateProductServlet")
public class ActivateProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            ProductDAO productDAO = new ProductDAO();
            productDAO.activateProduct(productId);
            
            // Reindirizza alla pagina di gestione
            response.sendRedirect(request.getContextPath() + "/AdminProductsServlet?status=activated");
        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Errore nella riattivazione del prodotto", e);
        }
    }
}