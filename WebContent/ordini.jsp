<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.Order, model.OrderItem, java.time.*, java.time.temporal.ChronoUnit" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    // Recupero dei dati e dei messaggi dalla sessione e dalla richiesta
    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    session.removeAttribute("successMessage");
    session.removeAttribute("errorMessage");
    
    // Rende i dati disponibili a JSTL
    pageContext.setAttribute("orders", request.getAttribute("orders"));
    pageContext.setAttribute("userReviewed", request.getAttribute("userReviewed"));
    pageContext.setAttribute("successMessage", successMessage);
    pageContext.setAttribute("errorMessage", errorMessage);
%>

<%@ include file="fragments/header.jspf" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style_pages.css">
<%@ include file="fragments/navbar.jspf" %>

<main class="page-container">
    <div class="orders-container">
        <h2>I Miei Ordini</h2>
        
        <c:if test="${not empty successMessage}"><div class="alert-modern alert-success">${successMessage}</div></c:if>
        <c:if test="${not empty errorMessage}"><div class="alert-modern alert-danger">${errorMessage}</div></c:if>

        <c:choose>
            <c:when test="${not empty orders}">
                <div class="accordion mt-4" id="ordersAccordion">
                    <c:forEach var="order" items="${orders}" varStatus="loop">
                        
                        <c:set var="orderDate" value="${order.orderDate}" />
                        <%
                            java.util.Date date = (java.util.Date) pageContext.getAttribute("orderDate");
                            if (date != null) {
                                pageContext.setAttribute("daysSinceDelivery", ChronoUnit.DAYS.between(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()));
                            }
                        %>

                        <div class="accordion-item">
                            <h2 class="accordion-header" id="heading-${loop.index}">
                                <button class="accordion-button ${loop.index > 0 ? 'collapsed' : ''}" type="button" data-bs-toggle="collapse" data-bs-target="#collapse-${loop.index}">
                                    Ordine #${order.id}
                                    <span class="ms-auto me-2">Data: <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></span>
                                    <span class="status-badge ${order.status.toLowerCase().replace(' ', '-')}">
                                        <c:choose>
                                            <c:when test="${order.status.equalsIgnoreCase('In preparazione')}"><span class="material-icons" style="font-size: 1.1em;">inventory</span></c:when>
                                            <c:when test="${order.status.equalsIgnoreCase('in consegna') or order.status.equalsIgnoreCase('spedito')}"><span class="material-icons" style="font-size: 1.1em;">local_shipping</span></c:when>
                                            <c:when test="${order.status.equalsIgnoreCase('consegnato')}"><span class="material-icons" style="font-size: 1.1em;">task_alt</span></c:when>
                                            <c:when test="${order.status.equalsIgnoreCase('restituito')}"><span class="material-icons" style="font-size: 1.1em;">assignment_return</span></c:when>
                                            <c:when test="${order.status.equalsIgnoreCase('annullato')}"><span class="material-icons" style="font-size: 1.1em;">cancel</span></c:when>
                                        </c:choose>
                                        ${order.status}
                                    </span>
                                </button>
                            </h2>
                            <div id="collapse-${loop.index}" class="accordion-collapse collapse ${loop.index == 0 ? 'show' : ''}" data-bs-parent="#ordersAccordion">
                                <div class="accordion-body">
                                    <p><strong>Totale Ordine: <fmt:formatNumber value="${order.total}" type="currency" currencySymbol="€ "/></strong></p>
                                    
                                    <ul class="order-items-list">
                                        <c:forEach var="item" items="${order.items}">
                                            <c:set var="recensito" value="${not empty userReviewed && userReviewed[item.productId]}" />
                                            <li>
                                                <img src="${pageContext.request.contextPath}/${item.productImg}" alt="${item.productName}" class="order-item-img">
                                                <div class="order-item-details">
                                                    <strong>${item.productName}</strong><br>
                                                    <small>Quantità: ${item.quantity} - Prezzo: <fmt:formatNumber value="${item.price}" type="currency" currencySymbol="€ "/></small>
                                                </div>
                                                
                                                <c:if test="${order.status.equalsIgnoreCase('consegnato') && !recensito}">
                                                    <button type="button" class="btn-review" 
                                                            data-product-id="${item.productId}"
                                                            data-product-name="${item.productName}">
                                                        Recensisci
                                                    </button>
                                                </c:if>
                                                <c:if test="${recensito}"><span class="badge-reviewed">✔ Recensito</span></c:if>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                    
                                    <!-- Bottone per scaricare la fattura PDF -->
<form method="get" action="GeneraFatturaServlet" target="_blank" style="display:inline;">
    <input type="hidden" name="orderId" value="${order.id}" />
    <button type="submit" class="btn btn-outline-secondary" style="margin-top: 10px;">
        📄 Scarica Fattura
    </button>
</form>
                                    <div class="mt-4 text-end">
                                        <c:if test="${order.status.equalsIgnoreCase('in preparazione')}">
                                            <form action="AnnullaOrdineServlet" method="post" style="display:inline;" onsubmit="return confirm('Sei sicuro di voler annullare questo ordine?');">
                                                <input type="hidden" name="orderId" value="${order.id}">
                                                <button type="submit" class="btn-danger" style="width: auto; padding: 0.6em 1.2em;">Annulla Ordine</button>
                                            </form>
                                        </c:if>
                                        <c:if test="${order.status.equalsIgnoreCase('consegnato') && daysSinceDelivery < 30}">
                                            <form action="ResoOrdineServlet" method="post" style="display:inline;" onsubmit="return confirm('Sei sicuro di voler richiedere il reso per questo ordine?');">
                                                <input type="hidden" name="orderId" value="${order.id}">
                                                <button type="submit" class="btn-action" style="width: auto; padding: 0.6em 1.2em;">Richiedi Reso</button>
                                            </form>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="alert-modern alert-info mt-4">Nessun ordine trovato.</div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<div class="modal fade" id="reviewModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content modal-content-modern">
      <form id="reviewForm" action="SubmitReviewServlet" method="post">
        <div class="modal-header modal-header-modern">
          <h5 class="modal-title" id="reviewModalTitle">Lascia una recensione</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <p class="text-center fs-5" id="reviewProductName"></p>
          <div class="mb-3 text-center" id="reviewStars">
              <% for(int i=1; i<=5; i++) { %>
                  <span class="star" data-value="<%= i %>">☆</span>
              <% } %>
          </div>
          <input type="hidden" name="productId" id="reviewProductId">
          <input type="hidden" name="rating" id="reviewRating" value="0">
          <textarea class="form-control form-control-modern" name="comment" id="reviewComment" placeholder="Il tuo commento (opzionale)..." rows="3"></textarea>
        </div>
        <div class="modal-footer modal-footer-modern">
          <button type="button" class="btn-secondary" data-bs-dismiss="modal">Chiudi</button>
          <button type="submit" class="btn-action">Invia Recensione</button>
        </div>
      </form>
    </div>
  </div>
</div>

<%@ include file="fragments/footer.jspf" %>
<script src="<%= request.getContextPath() %>/js/ordini.js"></script>