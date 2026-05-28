package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.CartDAO;
import model.CartItem;
import model.Order;
import model.OrderDAO;
import model.OrderItem;
import util.EncryptionUtil;

@WebServlet("/ProcessOrderServlet")
public class ProcessOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Pattern per la validazione
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^\\d{16}$");
    private static final Pattern CARD_EXPIRY_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])\\s/\\s\\d{2}$");
    private static final Pattern CARD_CVV_PATTERN = Pattern.compile("^\\d{3,4}$");
    private static final Pattern CARD_HOLDER_NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s']+$");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?returnUrl=checkout.jsp");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        
        String shippingAddress = request.getParameter("shippingAddress");
        String cardHolderName = request.getParameter("cardHolderName");
        // Rimuoviamo gli spazi dal numero di carta prima di inviarlo
        String cardNumber = request.getParameter("cardNumber").replaceAll("\\s", ""); 
        String cardExpiry = request.getParameter("cardExpiry");
        String cardCvv = request.getParameter("cardCvv");

        // Validazione dei dati lato server
        boolean isValid = true;
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) isValid = false;
        if (cardHolderName == null || !CARD_HOLDER_NAME_PATTERN.matcher(cardHolderName).matches()) isValid = false;
        if (cardNumber == null || !CARD_NUMBER_PATTERN.matcher(cardNumber).matches()) isValid = false;
        if (cardExpiry == null || !CARD_EXPIRY_PATTERN.matcher(cardExpiry).matches()) isValid = false;
        if (cardCvv == null || !CARD_CVV_PATTERN.matcher(cardCvv).matches()) isValid = false;

        if (!isValid) {
            // Se anche solo un campo non è valido blocca il processo
            response.sendRedirect(request.getContextPath() + "/checkout.jsp?error=invalid_data");
            return;
        }

        CartDAO cartDAO = new CartDAO();
        OrderDAO orderDAO = new OrderDAO();

        try {
            List<CartItem> cart = cartDAO.getCartForUser(userId);
            if (cart == null || cart.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/CartServlet?error=empty");
                return;
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setOrderDate(new Date());
            order.setStatus("In preparazione");
            order.setShippingAddress(shippingAddress);

            order.setCardHolderName(cardHolderName);
            order.setCardExpiryDate(cardExpiry);
            order.setCardLastFourDigits(cardNumber.substring(cardNumber.length() - 4));
            order.setEncryptedCardNumber(EncryptionUtil.encrypt(cardNumber));

            double total = cart.stream().mapToDouble(CartItem::getTotal).sum();
            Double discount = (Double) session.getAttribute("discount");
            if (discount != null && discount > 0.0) {
                total = total * (1 - discount);
            }
            order.setTotal(total);
            
            List<OrderItem> orderItems = new ArrayList<>();
            for(CartItem ci : cart) {
                OrderItem oi = new OrderItem();
                oi.setProductId(ci.getProductId());
                oi.setQuantity(ci.getQuantity());
                oi.setPrice(ci.getPrice());
                orderItems.add(oi);
            }
            order.setItems(orderItems);

            orderDAO.createOrder(order);

            cartDAO.clearCartForUser(userId);
            session.removeAttribute("coupon");
            session.removeAttribute("discount");

            response.sendRedirect(request.getContextPath() + "/ProductServlet?order=success");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/checkout.jsp?error=db_error");
        }
    }
}