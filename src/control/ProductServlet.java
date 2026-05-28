package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Product;
import model.ProductDAO;

@WebServlet("/ProductServlet")
public class ProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ProductServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        ProductDAO productDAO = new ProductDAO();

        String viewMode = request.getParameter("view");

        try {
            // Se il parametro è traditional
            if ("traditional".equals(viewMode)) {
                List<Product> products = productDAO.getAllProducts();
                List<String> departments = productDAO.getAllDepartments();

                request.setAttribute("traditionalProducts", products);
                request.setAttribute("departments", departments);

                RequestDispatcher dispatcher = request.getRequestDispatcher("/traditional-shop.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // se il parametro è mancante o null
            if (viewMode == null) {
                List<Product> products = productDAO.getAllProducts();

                @SuppressWarnings("unchecked")
                List<Integer> randomOrder = (List<Integer>) session.getAttribute("randomProductOrder");
                if (randomOrder == null || "1".equals(request.getParameter("shuffle")) || randomOrder.size() != products.size()) {
                    randomOrder = new ArrayList<>();
                    for (int i = 0; i < products.size(); i++) {
                        randomOrder.add(i);
                    }
                    Collections.shuffle(randomOrder);
                    session.setAttribute("randomProductOrder", randomOrder);
                }

                List<Product> productsShuffled = new ArrayList<>();
                for (int idx : randomOrder) {
                    if (idx < products.size()) {
                        productsShuffled.add(products.get(idx));
                    }
                }
                
                request.setAttribute("products", productsShuffled);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/products.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // se il parametro non è tratitional
            else {
                request.setAttribute("errorMessage", "Pagina non trovata. Il parametro 'view' non è valido.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
                dispatcher.forward(request, response);
                return;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore di database nel ProductServlet", e);
            throw new ServletException("Impossibile recuperare i dati dal database.", e);
        }
    }
}