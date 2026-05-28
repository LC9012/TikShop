package control;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/UpdateProfileServlet")

@SuppressWarnings("serial")
public class UpdateProfileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String newName = request.getParameter("name");
        String newEmail = request.getParameter("email");
        String newPassword = request.getParameter("password");

        // Pulizia input
        newName = newName != null ? newName.trim() : "";
        newEmail = newEmail != null ? newEmail.trim() : "";

        // Validazione base
        if (newName.isEmpty() || newEmail.isEmpty()) {
            response.sendRedirect("profilo.jsp?error=campi_obbligatori");
            return;
        }

        try (Connection conn = util.DBUtil.getConnection()) {
            String sql;
            PreparedStatement stmt;
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                // Cifrazione password
                sql = "UPDATE users SET name=?, email=?, password=? WHERE id=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, newName);
                stmt.setString(2, newEmail);
                stmt.setString(3, newPassword);
                stmt.setInt(4, userId);
            } else {
                sql = "UPDATE users SET name=?, email=? WHERE id=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, newName);
                stmt.setString(2, newEmail);
                stmt.setInt(3, userId);
            }
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                // Aggiorna i dati in sessione
                session.setAttribute("username", newName);
                response.sendRedirect("profilo.jsp?success=1");
            } else {
                response.sendRedirect("profilo.jsp?error=1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("profilo.jsp?error=1");
        }
    }
}