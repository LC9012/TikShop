package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Review;
import model.ReviewDAO;

@WebServlet(name = "ProductReviewsServlet", urlPatterns = {"/ProductReviewsServlet", "/AddReviewServlet"})
public class ProductReviewsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String productIdStr = req.getParameter("productId");
        String ratingStr = req.getParameter("rating");

        if (productIdStr == null || productIdStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID mancante.");
            return;
        }

        ReviewDAO reviewDAO = new ReviewDAO();
        JSONObject responseJson = new JSONObject();

        try {
            int productId = Integer.parseInt(productIdStr);
            //Calcolo della media recensioni del prodotto
            List<Review> allReviews = reviewDAO.getReviewsByProductId(productId, null);
            List<Review> filteredReviews = ratingStr == null || ratingStr.equalsIgnoreCase("ALL") ? allReviews : reviewDAO.getReviewsByProductId(productId, ratingStr);

            double avgRating = allReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            int reviewsCount = allReviews.size();

            JSONArray reviewsJson = new JSONArray();
            for (Review review : filteredReviews) {
                JSONObject obj = new JSONObject();
                String name = review.getUserName();
                String censoredName = (name != null && name.length() > 3) ? name.substring(0, 3) + "***" : (name != null ? name : "");
                
                obj.put("user", censoredName);
                obj.put("rating", review.getRating());
                obj.put("comment", review.getComment() != null ? review.getComment() : "");
                reviewsJson.put(obj);
            }
            
            responseJson.put("reviews", reviewsJson);
            responseJson.put("avgRating", avgRating);
            responseJson.put("reviewsCount", reviewsCount);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID prodotto non valido.");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Errore del database durante il recupero delle recensioni.", e);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(responseJson.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        JSONObject responseJson = new JSONObject();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (session == null || session.getAttribute("userId") == null) {
            responseJson.put("success", false).put("message", "Devi essere loggato per lasciare una recensione.");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(responseJson.toString());
            return;
        }

        try {
            int userId = (Integer) session.getAttribute("userId");
            int productId = Integer.parseInt(req.getParameter("productId"));
            int rating = Integer.parseInt(req.getParameter("rating"));
            String comment = req.getParameter("comment");

            if (rating < 1 || rating > 5) {
                throw new NumberFormatException("Il rating deve essere tra 1 e 5.");
            }
            
            ReviewDAO reviewDAO = new ReviewDAO();
            boolean success = reviewDAO.addReview(userId, productId, rating, comment);

            if (success) {
                responseJson.put("success", true).put("message", "Recensione inviata con successo!");
            } else {
                responseJson.put("success", false).put("message", "Hai già recensito questo prodotto.");
            }
            resp.getWriter().write(responseJson.toString());

        } catch (NumberFormatException e) {
            responseJson.put("success", false).put("message", "Dati non validi. Assicurati di aver selezionato un voto.");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(responseJson.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            responseJson.put("success", false).put("message", "Errore del server durante il salvataggio della recensione.");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(responseJson.toString());
        }
    }
}