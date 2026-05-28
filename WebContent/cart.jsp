
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.CartItem, java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Calcolo dei totali in Java --%>
<c:set var="cart" value="${requestScope.cart}" />
<c:set var="total" value="0" />
<c:forEach var="item" items="${cart}">
    <c:set var="total" value="${total + item.total}" />
</c:forEach>
<c:set var="discountRate" value="${not empty discount ? discount : 0.0}" />
<c:set var="discountAmount" value="${total * discountRate}" />
<c:set var="totalDiscounted" value="${total - discountAmount}" />

<%-- header  --%>
<%@ include file="fragments/header.jspf" %>

<%-- CSS per le pagine  --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style_pages.css">

<%-- Includiamo la navbar --%>
<%@ include file="fragments/navbar.jspf" %>

<main class="page-container">
    <div class="cart-page-layout">
        
        <!-- Colonna lista prodotti -->
        <div class="cart-items-container">
            <h2>Il Tuo Carrello</h2>

            <c:if test="${empty cart}">
                <div class="alert-modern alert-info">
                    Il tuo carrello è vuoto. <a href="${pageContext.request.contextPath}/ProductServlet" class="privacy-link">Torna allo shopping</a>!
                </div>
            </c:if>

            <c:if test="${not empty cart}">
                <c:forEach var="item" items="${cart}">
                    <div class="cart-item-card">
                        <img src="${pageContext.request.contextPath}/${item.fotoUrl}" class="cart-item-img" alt="${item.name}">
                        
                        <div class="cart-item-info">
                            <h5><c:out value="${item.name}"/></h5>
                            <p class="price mb-2">
                                <fmt:formatNumber value="${item.price}" type="currency" currencySymbol="€ " />
                            </p>
                            
                            <!-- Controllo Quantità -->
                            <div class="quantity-control">
                                <form method="post" action="<c:url value='/CartServlet'/>" class="d-inline">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    <input type="hidden" name="quantity" value="${item.quantity - 1}">
                                    <button type="submit" class="btn" ${item.quantity <= 1 ? "disabled" : ""}>−</button>
                                </form>
                                <span class="fw-bold fs-5 mx-1">${item.quantity}</span>
                                <form method="post" action="<c:url value='/CartServlet'/>" class="d-inline">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    <input type="hidden" name="quantity" value="${item.quantity + 1}">
                                    <button type="submit" class="btn">+</button>
                                </form>
                            </div>
                        </div>

                        <div class="item-total-price">
                            <fmt:formatNumber value="${item.total}" type="currency" currencySymbol="€ " />
                        </div>

                        <!-- Pulsante Rimuovi -->
                        <form method="post" action="<c:url value='/CartServlet'/>">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="productId" value="${item.productId}">
                            <button type="submit" class="remove-item-btn" title="Rimuovi prodotto">
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-x-lg" viewBox="0 0 16 16"><path d="M2.146 2.854a.5.5 0 1 1 .708-.708L8 7.293l5.146-5.147a.5.5 0 0 1 .708.708L8.707 8l5.147 5.146a.5.5 0 0 1-.708.708L8 8.707l-5.146 5.147a.5.5 0 0 1-.708-.708L7.293 8 2.146 2.854Z"/></svg>
                            </button>
                        </form>
                    </div>
                </c:forEach>
            </c:if>
        </div>

        <!-- colonna laterale riepilogo ordine -->
        <c:if test="${not empty cart}">
            <div class="order-summary-container">
                <h2>Riepilogo</h2>
                
                <div class="summary-row">
                    <span>Subtotale</span>
                    <strong><fmt:formatNumber value="${total}" type="currency" currencySymbol="€ " /></strong>
                </div>

                <c:if test="${discountRate > 0.0}">
                    <div class="summary-row discount">
                        <span>Sconto (<fmt:formatNumber value="${discountRate}" type="percent" />)</span>
                        <strong>-<fmt:formatNumber value="${discountAmount}" type="currency" currencySymbol="€ " /></strong>
                    </div>
                </c:if>

                <div class="summary-row total">
                    <span>TOTALE</span>
                    <span><fmt:formatNumber value="${totalDiscounted}" type="currency" currencySymbol="€ " /></span>
                </div>

                <!-- Form Coupon -->
                <form class="coupon-form" method="post" action="<c:url value='/CartServlet'/>">
                    <input type="hidden" name="action" value="coupon">
                    <input type="text" name="coupon" class="form-control form-control-modern coupon-input" placeholder="Codice Sconto" value="${coupon}">
                    <button type="submit" class="btn-action" style="width: auto; padding: 0.6em 1em;">Applica</button>
                </form>
                <c:if test="${not empty couponMsg}">
                    <div class="mt-2 small ${discountRate > 0.0 ? 'text-success' : 'text-danger'}">${couponMsg}</div>
                </c:if>

                <!-- Pulsante Checkout -->
                <div class="mt-4 d-grid">
                     <c:choose>
                        <c:when test="${not empty sessionScope.userId}">
                            <a href="<c:url value='/checkout.jsp'/>" class="btn-action btn-lg">Procedi al Checkout</a>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:url value='/login.jsp'><c:param name='returnUrl' value='${pageContext.request.contextPath}/cart.jsp'/></c:url>" class="btn-action btn-lg">Accedi per Continuare</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:if>
    </div>
</main>


<%@ include file="fragments/footer.jspf" %>