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
import org.mindrot.jbcrypt.BCrypt;
import model.User;
import model.UserDAO;
import model.CartDAO;
import model.CartItem;
@WebServlet("/RegisterServlet")

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String returnUrl = request.getParameter("returnUrl");

        if (name == null || email == null || password == null || name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            response.sendRedirect("register.jsp?error=campi_obbligatori");
            return;
        }

        UserDAO userDAO = new UserDAO();

        try {
            if (userDAO.emailExists(email.trim())) {
                response.sendRedirect("register.jsp?error=email_registrata");
                return;
            }

            User newUser = new User();
            newUser.setName(name.trim());
            newUser.setEmail(email.trim());
            newUser.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));

            User savedUser = userDAO.saveUser(newUser);

            HttpSession session = request.getSession(true);
            session.setAttribute("userId", savedUser.getId());
            session.setAttribute("username", savedUser.getName());
            
            // Migrazione carrello
            @SuppressWarnings("unchecked")
            List<CartItem> sessionCart = (List<CartItem>) session.getAttribute("cart");
            if (sessionCart != null && !sessionCart.isEmpty()) {
                CartDAO cartDAO = new CartDAO();
                cartDAO.mergeSessionCartWithDbCart(savedUser.getId(), sessionCart);
                session.removeAttribute("cart");
            }

            if (returnUrl != null && !returnUrl.isEmpty() && returnUrl.startsWith(request.getContextPath())) {
                response.sendRedirect(returnUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/ProductServlet?registration=success");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=errore_server");
        }
    }
}