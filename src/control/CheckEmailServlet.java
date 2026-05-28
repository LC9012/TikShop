package control;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.UserDAO;


@WebServlet("/CheckEmailServlet")
public class CheckEmailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        UserDAO userDAO = new UserDAO();
        boolean exists = false;

        // Impostiamo l'header della risposta a JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (email != null && !email.trim().isEmpty()) {
            try {
                exists = userDAO.emailExists(email.trim());
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Errore durante la verifica dell'email.\"}");
                e.printStackTrace();
                return;
            }
        }

        // Scriviamo la risposta JSON true o false in base a se esiste o meno
        response.getWriter().write("{\"exists\": " + exists + "}");
    }
}