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

@WebServlet("/AdminFilter")
public class AdminFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        System.out.println("--- ADMIN FILTER ATTIVATO per URL: " + httpRequest.getRequestURI() + " ---");

        if (session == null) {
            System.out.println("RISULTATO: Sessione non trovata. Accesso negato.");
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso Negato");
            return;
        }

        // Recuperiamo il ruolo dalla sessione
        String userRole = (String) session.getAttribute("userRole");
        System.out.println("RUOLO TROVATO IN SESSIONE: " + userRole);

        // Confronto esatto
        boolean isAdmin = "admin".equals(userRole);
        
        if (isAdmin) {
            System.out.println("RISULTATO: Utente è ADMIN. Accesso consentito.");
            chain.doFilter(request, response);
        } else {
            System.out.println("RISULTATO: Utente NON è ADMIN. Accesso negato.");
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso Negato");
        }
    }
  
}