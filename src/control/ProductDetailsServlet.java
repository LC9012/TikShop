package control;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@SuppressWarnings("serial")
@WebServlet("/ProductDetailsServlet")
public class ProductDetailsServlet extends HttpServlet {

    // Nested class per le recensioni 
    public static class Review {
        private String user;
        private String comment;
        private int rating;
        public Review(String user, String comment, int rating) {
            this.user = user;
            this.comment = comment;
            this.rating = rating;
        }
        public String getUser() { return user; }
        public String getComment() { return comment; }
        public int getRating() { return rating; }
    }

    // Nested class per i dettagli del prodotto 
    public static class ProductDetails {
        private int id;
        private String name;
        private String videoUrl; 
        private String fotoUrl;  
        private double price;
        private String description;
  
        public ProductDetails(int id, String name, String videoUrl, String fotoUrl, String description, double price) {
            this.id = id;
            this.name = name;
            this.videoUrl = videoUrl;
            this.fotoUrl = fotoUrl;
            this.description = description;
            this.price = price;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getVideoUrl() { return videoUrl; }
        public String getFotoUrl() { return fotoUrl; }
        public String getDescription() { return description; }
        public double getPrice() { return price; }
        
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pidStr = request.getParameter("id");
        if (pidStr == null) {
            response.sendRedirect("ProductServlet"); 
            return;
        }
        int pid = Integer.parseInt(pidStr);
        ProductDetails details = null;

        try (Connection conn = util.DBUtil.getConnection()) {
            String sql = "SELECT name, description, price, video_url, foto FROM products WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, pid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                String videoUrl = rs.getString("video_url");
                String fotoUrl = rs.getString("foto"); 

                // Inizializza ProductDetails con i dati di base
                details = new ProductDetails(pid, name, videoUrl, fotoUrl, description, price);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ProductServlet"); 
            return;
        }

        if (details == null) {
            response.sendRedirect("ProductServlet"); 
            return;
        }

        request.setAttribute("product", details);
        request.getRequestDispatcher("product_details.jsp").forward(request, response);
    }
}