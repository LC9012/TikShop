
package control;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
@WebServlet("/AuthenticationFilter")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);
        
        boolean isLoggedIn = (session != null && session.getAttribute("userId") != null);

        if (isLoggedIn) {
            // Utente loggato, la richiesta può proseguire.
            chain.doFilter(request, response);
        } else {
            // Utente non loggato, reindirizza al login.
            String requestURI = httpRequest.getRequestURI();
            String queryString = httpRequest.getQueryString();
            String targetUrl = requestURI + (queryString != null ? "?" + queryString : "");
            
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?returnUrl=" + java.net.URLEncoder.encode(targetUrl, "UTF-8"));
        }
    }

    @Override
    public void destroy() {
        
    }
}