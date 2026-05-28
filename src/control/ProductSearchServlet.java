package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Product;
import model.ProductDAO;

@WebServlet("/ProductSearchServlet")
public class ProductSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("q");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (query == null || query.trim().isEmpty()) {
            out.print("[]"); // Restituisce un array JSON vuoto se la ricerca è vuota
            out.flush();
            return;
        }

        ProductDAO productDAO = new ProductDAO();
        try {
            List<Product> products = productDAO.searchProductsByName(query);
            
            // Costruiamo manualmente la stringa JSON
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                json.append("{");
                json.append("\"id\":").append(p.getId()).append(",");
                json.append("\"name\":\"").append(escapeJson(p.getName())).append("\",");
                json.append("\"fotoUrl\":\"").append(escapeJson(p.getFotoUrl())).append("\"");
                json.append("}");
                if (i < products.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            
            out.print(json.toString());
            out.flush();

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Errore del database\"}");
            out.flush();
        }
    }

    // Funzione di utility per escapare caratteri speciali in JSON
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b")
                    .replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}