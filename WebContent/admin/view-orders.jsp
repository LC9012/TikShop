<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- We include the header --%>
<%@ include file="../fragments/header.jspf" %>

<%-- We include the CSS for the internal pages (which contains the dark style) --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style_pages.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">

<%-- We include the navbar --%>
<%@ include file="../fragments/navbar.jspf" %>

<main class="page-container">
    <div class="admin-container">
        <div class="admin-header">
            <h1>Gestione Ordini</h1>
            <div class="action-buttons">
                <a href="<c:url value='/admin/upload-product.jsp'/>" class="btn-secondary"><i class="fas fa-plus"></i> Aggiungi Prodotto</a>
                <a href="<c:url value='/AdminProductsServlet'/>" class="btn-action"><i class="fas fa-box-open"></i> Gestione Prodotti</a>
            </div>
        </div>

        <div class="filters-bar">
            <div class="filter-group">
                <label for="statusFilter">Filtra per Stato:</label>
                <form action="AdminOrdersServlet" method="get" id="filterForm" style="display: inline;">
                    <input type="hidden" name="sort" value="${currentSort}">
                    <input type="hidden" name="order" value="${currentOrder}">
                    
                    <select name="status" id="statusFilter" class="form-control-modern" onchange="document.getElementById('filterForm').submit();">
                        <option value="all" ${currentStatus == 'all' ? 'selected' : ''}>Tutti</option>
                        <option value="In preparazione" ${currentStatus == 'In preparazione' ? 'selected' : ''}>In preparazione</option>
                        <option value="In consegna" ${currentStatus == 'In consegna' ? 'selected' : ''}>In consegna</option>
                        <option value="Completato" ${currentStatus == 'Completato' ? 'selected' : ''}>Completato</option>
                        <option value="Annullato" ${currentStatus == 'Annullato' ? 'selected' : ''}>Annullato</option>
                        <option value="Restituito" ${currentStatus == 'Restituito' ? 'selected' : ''}>Restituito</option>
                    </select>
                </form>
            </div>
        </div>
        <div class="admin-table-wrapper">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID Ordine</th>
                        <th>
                            <a href="AdminOrdersServlet?sort=name&order=${currentSort == 'name' && currentOrder == 'asc' ? 'desc' : 'asc'}&status=${currentStatus}">
                                Cliente <i class="fas fa-sort"></i>
                            </a>
                        </th>
                        <th>
                            <a href="AdminOrdersServlet?sort=date&order=${currentSort == 'date' && currentOrder == 'asc' ? 'desc' : 'asc'}&status=${currentStatus}">
                                Data <i class="fas fa-sort"></i>
                            </a>
                        </th>
                        <th>
                             <a href="AdminOrdersServlet?sort=status&order=${currentSort == 'status' && currentOrder == 'asc' ? 'desc' : 'asc'}&status=${currentStatus}">
                                Stato <i class="fas fa-sort"></i>
                            </a>
                        </th>
                        <th style="text-align: right;">Totale</th>
                        <th style="text-align: center;">Azioni</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <%-- MAIN ORDER ROW --%>
                        <tr>
                            <td class="order-id">#<c:out value="${order.id}"/></td>
                            <td>
                                <span class="customer-name"><c:out value="${order.customerName}"/></span><br>
                                <small class="text-muted">ID: ${order.userId}</small>
                            </td>
                            <td><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td>
                                <c:set var="statusClass" value="${order.status.toLowerCase().replace(' ', '-')}"/>
                                <span class="status-badge-admin ${statusClass}"><c:out value="${order.status}"/></span>
                            </td>
                            <td style="text-align: right;" class="fw-bold fs-5">
                                <fmt:formatNumber value="${order.total}" type="currency" currencySymbol="€ "/>
                            </td>
                            <td style="text-align: center;">
                                <button class="btn-details" type="button" data-bs-toggle="collapse" data-bs-target="#details-${order.id}">
                                    Dettagli <i class="fas fa-chevron-down"></i>
                                </button>
                            </td>
                        </tr>
                        
                        <%-- COLLAPSIBLE DETAILS ROW (RESTORED) --%>
                        <tr class="order-details-row">
                            <td colspan="6">
                                <div class="collapse" id="details-${order.id}">
                                    <div class="order-details-content">
                                        <div class="row">
                                            <div class="col-md-6">
                                                <h6>Dettagli Spedizione e Cliente</h6>
                                                <div class="detail-item">
                                                    <span class="detail-label">Cliente:</span>
                                                    <span class="detail-value"><c:out value="${order.customerName}"/></span>
                                                </div>
                                                <div class="detail-item">
                                                    <span class="detail-label">Indirizzo Spedizione:</span>
                                                    <span class="detail-value"><c:out value="${order.shippingAddress}"/></span>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <h6>Prodotti Ordinati (${order.items.size()})</h6>
                                                <ul class="order-items-list">
                                                    <c:forEach var="item" items="${order.items}">
                                                        <li>
                                                            <img src="${pageContext.request.contextPath}/${item.productImg}" alt="${item.productName}" class="order-item-img">
                                                            <div class="order-item-details">
                                                                <strong><c:out value="${item.productName}"/></strong><br>
                                                                <small>Qtà: ${item.quantity} x <fmt:formatNumber value="${item.price}" type="currency" currencySymbol="€ "/></small>
                                                            </div>
                                                        </li>
                                                    </c:forEach>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</main>

<%@ include file="../fragments/footer.jspf" %>