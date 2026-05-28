package control;

import model.CartItem;
import model.ProductDAO;
import model.CartDAO;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/CartServlet")
@SuppressWarnings("serial")
public class CartServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CartServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        Integer userId = (Integer) session.getAttribute("userId");
        List<CartItem> cart = new ArrayList<>();
        CartDAO cartDAO = new CartDAO();

        try {
            if (userId != null) {
                cart = cartDAO.getCartForUser(userId); 
            } else {
                @SuppressWarnings("unchecked")
                List<CartItem> sessionCart = (List<CartItem>) session.getAttribute("cart");
                if (sessionCart != null) cart = sessionCart;
            }
        } catch (SQLException e) {
            LOGGER.severe("Errore DB nel recupero carrello: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare i dati del carrello.");
            return;
        }

        // Gestione mini-carrello con AJAX
        if ("1".equals(request.getParameter("mini"))) {
            if ("1".equals(request.getParameter("onlyCount"))) {
                int count = cart.stream().mapToInt(CartItem::getQuantity).sum();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"count\":" + count + "}");
                return;
            }
            
            response.setContentType("text/html;charset=UTF-8");
            if (cart.isEmpty()) {
                response.getWriter().write("<div class='text-secondary p-3'>Il carrello è vuoto.</div>");
            } else {
                double total = cart.stream().mapToDouble(CartItem::getTotal).sum();
                StringBuilder html = new StringBuilder("<ul class='list-group list-group-flush'>");
                for (CartItem item : cart) {
                    html.append("<li class='list-group-item d-flex justify-content-between align-items-center'>")
                        .append("<span><b>").append(item.getName()).append("</b><br><small>Quantità: ").append(item.getQuantity()).append("</small></span>")
                        .append("<span>€ ").append(String.format("%.2f", item.getTotal())).append("</span>")
                        .append("</li>");
                }
                html.append("</ul><div class='fw-bold p-3 text-end'>Totale: € ").append(String.format("%.2f", total)).append("</div>");
                response.getWriter().write(html.toString());
            }
            return;
        }

        // Dispatch alla pagina del carrello
        request.setAttribute("cart", cart);
        request.setAttribute("coupon", session.getAttribute("coupon"));
        request.setAttribute("discount", session.getAttribute("discount") != null ? session.getAttribute("discount") : 0.0);
        request.setAttribute("couponMsg", session.getAttribute("couponMsg"));
        request.getRequestDispatcher("/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        Integer userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");
        
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        boolean success = false;
        String errorMessage = "Azione fallita.";
        CartDAO cartDAO = new CartDAO();

        try {
            if (action == null) {
                throw new ServletException("Azione non specificata.");
            }
            
            switch (action) {
                case "add":
                    handleAdd(request, userId);
                    success = true;
                    break;
                case "remove":
                    handleRemove(request, userId);
                    success = true;
                    break;
                case "update":
                    handleUpdate(request, userId);
                    success = true;
                    break;
                case "coupon":
                    handleCoupon(request);
                    success = true;
                    break;
                default:
                    errorMessage = "Azione non riconosciuta: " + action;
                    LOGGER.warning(errorMessage);
            }
        } catch (SQLException e) {
            success = false;
            errorMessage = "Errore del database durante l'operazione.";
            LOGGER.severe(errorMessage + ": " + e.getMessage());
        } catch (Exception e) {
            success = false;
            errorMessage = "Parametri non validi o errore imprevisto.";
            LOGGER.warning(errorMessage + ": " + e.getMessage());
        }

        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            int count = 0;
            if (success) {
                try {
                    if (userId != null) {
                        count = cartDAO.getCartForUser(userId).stream().mapToInt(CartItem::getQuantity).sum();
                    } else {
                        @SuppressWarnings("unchecked")
                        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
                        if (cart != null) {
                            count = cart.stream().mapToInt(CartItem::getQuantity).sum();
                        }
                    }
                } catch(SQLException e) {
                     LOGGER.warning("Impossibile ricalcolare totale carrello: " + e.getMessage());
                }
            }
            
            String jsonResponse = String.format("{\"success\": %b, \"count\": %d, \"message\": \"%s\"}", success, count, success ? "Operazione completata" : errorMessage.replace("\"", "'"));
            response.getWriter().write(jsonResponse);
        } else {
            response.sendRedirect(request.getContextPath() + "/CartServlet");
        }
    }
    
   
    private void handleAdd(HttpServletRequest request, Integer userId) throws SQLException, NumberFormatException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        if (quantity <= 0) {
            return;
        }

        if (userId != null) {
            // Se l'utente è loggato, usa il DAO per gestire l'aggiunta e aggiorna nel DB
            new CartDAO().addOrUpdateProduct(userId, productId, quantity);
        } else {
            // Se l'utente non è loggato, gestisce il carrello nella sessione
            HttpSession session = request.getSession();
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
            }

            // Cerca se il prodotto è già nel carrello della sessione
            boolean foundInCart = false;
            for (CartItem item : cart) {
                if (item.getProductId() == productId) {
                    item.setQuantity(item.getQuantity() + quantity);
                    foundInCart = true;
                    break;
                }
            }

            // Se non è stato trovato lo recupera dal DB e lo aggiunge al carrello
            if (!foundInCart) {
                ProductDAO productDAO = new ProductDAO();
                CartItem newItem = productDAO.getProductByIdForCart(productId);
                
                if (newItem != null) {
                    newItem.setQuantity(quantity);
                    cart.add(newItem);
                } else {
                    // Lancia un errore se si tenta di aggiungere un prodotto non esistente
                    throw new SQLException("Prodotto con ID " + productId + " non trovato.");
                }
            }
            // Aggiorna il carrello nella sessione
            session.setAttribute("cart", cart);
        }
    }
    
    private void handleRemove(HttpServletRequest request, Integer userId) throws SQLException, NumberFormatException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        if (userId != null) {
            new CartDAO().removeProduct(userId, productId);
        } else {
            HttpSession session = request.getSession();
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart != null) {
                cart.removeIf(item -> item.getProductId() == productId);
                session.setAttribute("cart", cart);
            }
        }
    }
    
    private void handleUpdate(HttpServletRequest request, Integer userId) throws SQLException, NumberFormatException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        
        if (quantity <= 0) {
            handleRemove(request, userId);
            return;
        }

        if (userId != null) {
            new CartDAO().updateQuantity(userId, productId, quantity);
        } else {
            HttpSession session = request.getSession();
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart != null) {
                cart.stream()
                    .filter(item -> item.getProductId() == productId)
                    .findFirst()
                    .ifPresent(item -> {
                        item.setQuantity(quantity);
                        session.setAttribute("cart", cart);
                    });
            }
        }
    }

    private void handleCoupon(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String couponCode = request.getParameter("coupon");
        if (couponCode != null && couponCode.equalsIgnoreCase("TikShop")) {
            session.setAttribute("coupon", couponCode);
            session.setAttribute("discount", 0.10);
            session.setAttribute("couponMsg", "Coupon TikShop applicato! Sconto 10%.");
        } else
        if(couponCode != null && couponCode.equalsIgnoreCase("TikShop")) {
            session.setAttribute("coupon", couponCode);
            session.setAttribute("discount", 0.08);
            session.setAttribute("couponMsg", "Coupon TikShop applicato! Sconto 8%.");
        } 
        else{
            session.setAttribute("coupon", couponCode);
            session.setAttribute("discount", 0.0);
            session.setAttribute("couponMsg", "Coupon non valido.");
        }
    }
}