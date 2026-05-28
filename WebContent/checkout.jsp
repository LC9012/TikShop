<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.CartDAO, model.CartItem, java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Logica per recuperare il carrello e calcolare i totali --%>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
        response.sendRedirect("login.jsp?returnUrl=checkout.jsp");
        return;
    }
    
    CartDAO cartDAO = new CartDAO();
    List<CartItem> cartItems = cartDAO.getCartForUser(userId);
    request.setAttribute("cartItems", cartItems);

    double total = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
    double discountRate = (session.getAttribute("discount") != null) ? (Double) session.getAttribute("discount") : 0.0;
    double discountAmount = total * discountRate;
    double totalDiscounted = total - discountAmount;
    
    request.setAttribute("total", total);
    request.setAttribute("discountAmount", discountAmount);
    request.setAttribute("discountRate", discountRate);
    request.setAttribute("totalDiscounted", totalDiscounted);
%>

<%-- Includiamo l'header standard --%>
<%@ include file="fragments/header.jspf" %>

<%-- Includiamo il CSS per le pagine interne --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style_pages.css">

<%-- Includiamo la navbar --%>
<%@ include file="fragments/navbar.jspf" %>

<main class="page-container">
    <div class="checkout-page-layout">

        <!-- Colonna Principale Form di Checkout -->
        <div class="checkout-main-content">
            <h1>Checkout</h1>
            <p class="text-white">Completa i campi seguenti per finalizzare il tuo ordine.</p>

            <c:if test="${not empty param.error}">
                <div class="auth-message error">
                    Si è verificato un errore durante l'elaborazione. Ti preghiamo di riprovare.
                </div>
            </c:if>

            <form id="checkout-form" class="auth-form" action="<c:url value='/ProcessOrderServlet'/>" method="post" novalidate>
                <section class="form-section">
                    <h3>
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-truck" viewBox="0 0 16 16"><path d="M0 3.5A1.5 1.5 0 0 1 1.5 2h9A1.5 1.5 0 0 1 12 3.5V5h1.02a1.5 1.5 0 0 1 1.17.563l1.481 1.85a1.5 1.5 0 0 1 .329.938V10.5a1.5 1.5 0 0 1-1.5 1.5H14a2 2 0 1 1-4 0H5a2 2 0 1 1-4 0H1.5A1.5 1.5 0 0 1 0 10.5v-7zM12 5V3.5a.5.5 0 0 0-.5-.5h-9a.5.5 0 0 0-.5.5V5h10zM3 11a1 1 0 1 0 0-2 1 1 0 0 0 0 2zm9 0a1 1 0 1 0 0-2 1 1 0 0 0 0 2z"/></svg>
                        Indirizzo di Spedizione
                    </h3>
                    <div class="input-group">
                        <label for="shippingAddress">Indirizzo completo</label>
                        <textarea id="shippingAddress" name="shippingAddress" rows="3" required placeholder="Es: Via Roma 1, 00100 Roma (RM)" class="form-control-modern" maxlength="40"></textarea>
                        <div class="input-error-message" id="addressError"></div>
                    </div>
                </section>

                <section class="form-section">
                    <h3>
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-credit-card-2-front-fill" viewBox="0 0 16 16"><path d="M0 4a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V4zm2.5 1a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h2a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-2zm0 3a.5.5 0 0 0 0 1h5a.5.5 0 0 0 0-1h-5zm0 2a.5.5 0 0 0 0 1h1a.5.5 0 0 0 0-1h-1zm3 0a.5.5 0 0 0 0 1h1a.5.5 0 0 0 0-1h-1zm3 0a.5.5 0 0 0 0 1h1a.5.5 0 0 0 0-1h-1z"/></svg>
                        Dati di Pagamento
                    </h3>
                    <div class="input-group">
                        <label for="cardHolderName">Nome e Cognome sulla Carta</label>
                        <input type="text" id="cardHolderName" name="cardHolderName" required class="form-control-modern" maxlength="30">
                        <div class="input-error-message" id="cardNameError"></div>
                    </div>

                    <div class="input-group">
                        <label for="cardNumber">Numero Carta</label>
                        <input type="text" id="cardNumber" name="cardNumber" required placeholder="0000 0000 0000 0000" class="form-control-modern">
                        <div class="input-error-message" id="cardNumberError"></div>
                    </div>

                    <div class="form-row">
                        <div class="input-group">
                            <label for="cardExpiry">Scadenza (MM / YY)</label>
                            <input type="text" id="cardExpiry" name="cardExpiry" required placeholder="MM / YY" class="form-control-modern">
                            <div class="input-error-message" id="cardExpiryError"></div>
                        </div>

                        <div class="input-group">
                            <label for="cardCvv">CVV</label>
                            <input type="text" id="cardCvv" name="cardCvv" required placeholder="123" class="form-control-modern">
                            <div class="input-error-message" id="cardCvvError"></div>
                        </div>
                    </div>
                </section>

                <div class="mt-4">
                    <button type="submit" id="submit-btn" class="btn-action btn-lg" disabled>
                        Paga e Completa Ordine: <fmt:formatNumber value="${totalDiscounted}" type="currency" currencySymbol="€ "/>
                    </button>
                </div>
            </form>
        </div>

        <!-- Colonna Laterale Riepilogo Ordine -->
        <aside class="checkout-summary">
            <h2>Riepilogo Ordine</h2>
            
            <c:forEach var="item" items="${cartItems}">
                <div class="summary-item">
                    <span class="item-name">${item.quantity} x ${item.name}</span>
                    <span><fmt:formatNumber value="${item.total}" type="currency" currencySymbol="€ " /></span>
                </div>
            </c:forEach>
            
            <hr>
            
            <div class="summary-item">
                <span>Subtotale</span>
                <span><fmt:formatNumber value="${total}" type="currency" currencySymbol="€ " /></span>
            </div>
            
            <c:if test="${discountRate > 0.0}">
                <div class="summary-item discount">
                    <span>Sconto (<fmt:formatNumber value="${discountRate}" type="percent" />)</span>
                    <span>-<fmt:formatNumber value="${discountAmount}" type="currency" currencySymbol="€ " /></span>
                </div>
            </c:if>

            <hr>

            <div class="summary-total-row">
                <span class="total-label">Totale</span>
                <span><fmt:formatNumber value="${totalDiscounted}" type="currency" currencySymbol="€ " /></span>
            </div>
        </aside>
    </div>
</main>

<%-- Includiamo il footer standard --%>
<%@ include file="fragments/footer.jspf" %>

<%-- Includiamo lo script di validazione specifico per il checkout --%>
<script src="${pageContext.request.contextPath}/js/checkout.js"></script>