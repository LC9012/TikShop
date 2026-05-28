package control;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ProductDAO;

@WebServlet("/UpdatePriceServlet")
public class UpdatePriceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("productId");
        String priceStr = request.getParameter("newPrice");

        // Validazione di base
        if (idStr == null || priceStr == null || idStr.isEmpty() || priceStr.isEmpty()) {
            response.sendRedirect("AdminProductsServlet?error=invalid_data");
            return;
        }

        try {
            int productId = Integer.parseInt(idStr);
            // Sostituisce la virgola con il punto
            double newPrice = Double.parseDouble(priceStr.replace(',', '.'));

            if (newPrice < 0) {
                 response.sendRedirect("AdminProductsServlet?error=negative_price");
                 return;
            }
            
            ProductDAO productDAO = new ProductDAO();
            productDAO.updateProductPrice(productId, newPrice);

            // Redirect alla pagina di gestione con un messaggio di successo
            response.sendRedirect("AdminProductsServlet?status=price_updated");

        } catch (NumberFormatException e) {
            response.sendRedirect("AdminProductsServlet?error=invalid_number");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("AdminProductsServlet?error=db_error");
        }
    }
}