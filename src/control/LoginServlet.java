package control;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import model.CartDAO;
import model.CartItem;
import model.User;
import model.UserDAO;
@WebServlet("/LoginServlet")

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String returnUrl = request.getParameter("returnUrl");
        
        UserDAO userDAO = new UserDAO();
        User user = null;

        try {
            user = userDAO.getUserByEmail(email);

            if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
                HttpSession session = request.getSession();
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getName());
                session.setAttribute("userRole", user.getRole()); 

                @SuppressWarnings("unchecked")
                List<CartItem> sessionCart = (List<CartItem>) session.getAttribute("cart");
                if (sessionCart != null && !sessionCart.isEmpty()) {
                    CartDAO cartDAO = new CartDAO();
                    cartDAO.mergeSessionCartWithDbCart(user.getId(), sessionCart);
                    session.removeAttribute("cart");
                }

                if (returnUrl != null && !returnUrl.isEmpty() && returnUrl.startsWith(request.getContextPath())) {
                    response.sendRedirect(returnUrl);
                } else {
                    response.sendRedirect(request.getContextPath() + "/ProductServlet");
                }

            } else {
                redirectToLogin(request, response, "error=1");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            redirectToLogin(request, response, "error=server");
        }
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response, String params) throws IOException {
        String returnUrl = request.getParameter("returnUrl");
        String finalUrl = "login.jsp?" + params;
        if (returnUrl != null && !returnUrl.isEmpty()) {
            finalUrl += "&returnUrl=" + URLEncoder.encode(returnUrl, "UTF-8");
        }
        response.sendRedirect(finalUrl);
    }
}