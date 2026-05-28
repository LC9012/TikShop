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
import model.Product;
import model.ProductDAO;


@WebServlet("/AdminProductsServlet")
public class AdminProductsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductDAO productDAO = new ProductDAO();
        try {
            List<Product> productList = productDAO.getAllProductsForAdmin();
            request.setAttribute("products", productList);
            // Inoltra alla pagina JSP che mostra la tabella dei prodotti.
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/manage-products.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Errore DB nel recupero prodotti per admin", e);
        }
    }
}