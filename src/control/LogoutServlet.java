package control;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    HttpSession session = req.getSession(false);
	    if (session != null) {
	        session.removeAttribute("randomProductOrder");
	        session.invalidate();
	    }
	    resp.sendRedirect("ProductServlet");
	}
}