<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ include file="../fragments/header.jspf" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style_pages.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
<style>
    /* Styles for the search bar */
    .search-container {
        position: relative;
        margin-bottom: 1.5rem;
        max-width: 400px;
    }
    .search-input {
        width: 100%;
        padding: 10px 15px;
        border-radius: 25px;
        border: 1px solid #555;
        background-color: #3a3f4c;
        color: #f1f1f1;
        font-size: 1rem;
    }
    /* Style for the clickable price */
    .edit-price-btn {
        text-decoration: none;
        color: inherit;
        cursor: pointer;
        border-bottom: 2px dotted #70b2fa;
        transition: color 0.2s;
    }
    .edit-price-btn:hover {
        color: #a8e5c1;
    }
</style>
<%@ include file="../fragments/navbar.jspf" %>

<main class="page-container">
    <div class="admin-container">
        <div class="admin-header">
            <h1>Pannello di Controllo</h1>
            <div class="action-buttons">
                <a href="<c:url value='/AdminProductsServlet'/>" class="btn-action"><i class="fas fa-box-open"></i> Gestione Prodotti</a>
                <a href="<c:url value='/AdminOrdersServlet'/>" class="btn-secondary"><i class="fas fa-receipt"></i> Visualizza Ordini</a>
                <a href="<c:url value='/admin/upload-product.jsp'/>" class="btn-secondary"><i class="fas fa-plus"></i> Aggiungi Prodotto</a>
            </div>
        </div>

        <h2 class="mt-4" style="text-align: left; text-shadow: none; font-size: 1.5rem; color: #fff;">Lista Prodotti</h2>
        
        <div class="search-container">
            <input type="text" id="productSearchInput" class="search-input" placeholder="Filtra prodotti per nome...">
        </div>
        
        <c:if test="${param.status == 'price_updated'}"><div class="alert-modern alert-success">Prezzo aggiornato con successo.</div></c:if>
        <div class="admin-table-wrapper">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nome</th>
                        <th class="text-end">Prezzo</th>
                        <th class="text-center">Stato</th>
                        <th class="text-center">Azione</th>
                    </tr>
                </thead>
                <tbody id="productTableBody">
                    <c:forEach var="p" items="${products}">
                        <tr>
                            <td><strong>#${p.id}</strong></td>
                            <td class="product-name"><c:out value="${p.name}"/></td>
                            <td class="text-end fw-bold">
                                <a href="#" class="edit-price-btn" 
                                   data-product-id="${p.id}" 
                                   data-product-name="<c:out value="${p.name}"/>"
                                   data-product-price="${p.price}">
                                    <fmt:formatNumber value="${p.price}" type="currency" currencySymbol="€ "/>
                                </a>
                            </td>
                            <td class="text-center">
                                <c:if test="${p.active}"><span class="status-badge-admin active">Attivo</span></c:if>
                                <c:if test="${not p.active}"><span class="status-badge-admin inactive">Disattivo</span></c:if>
                            </td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${p.active}">
                                        <form action="<c:url value='/DeactivateProductServlet'/>" method="post" onsubmit="return confirm('Sei sicuro?');" style="margin:0;">
                                            <input type="hidden" name="productId" value="${p.id}">
                                            <button type="submit" class="btn-danger btn-sm">Disattiva</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="<c:url value='/ActivateProductServlet'/>" method="post" style="margin:0;">
                                            <input type="hidden" name="productId" value="${p.id}">
                                            <button type="submit" class="btn-success-outline btn-sm">Riattiva</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</main>

<div class="modal fade" id="priceModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content modal-content-modern">
      <form id="priceForm" action="UpdatePriceServlet" method="post">
        <div class="modal-header modal-header-modern">
          <h5 class="modal-title">Modifica Prezzo</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
            <input type="hidden" name="productId" id="modalProductId">
            <p>Prodotto: <strong id="modalProductName"></strong></p>
            <div class="mb-3">
              <label for="modalNewPrice" class="form-label">Nuovo Prezzo (€)</label>
              <input type="number" class="form-control form-control-modern" id="modalNewPrice" name="newPrice" step="0.01" required>
            </div>
        </div>
        <div class="modal-footer modal-footer-modern">
          <button type="button" class="btn-secondary" data-bs-dismiss="modal">Annulla</button>
          <button type="submit" class="btn-action">Salva Modifiche</button>
        </div>
      </form>
    </div>
  </div>
</div>

<%@ include file="../fragments/footer.jspf" %>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Lotica per cambiare il prezzo
    const priceModalElement = document.getElementById('priceModal');
    if (priceModalElement) {
        const priceModal = new bootstrap.Modal(priceModalElement);
        const modalProductId = document.getElementById('modalProductId');
        const modalProductName = document.getElementById('modalProductName');
        const modalNewPrice = document.getElementById('modalNewPrice');

        document.body.addEventListener('click', function(event) {
            if (event.target.classList.contains('edit-price-btn')) {
                event.preventDefault();
                const button = event.target;
                
                const productId = button.getAttribute('data-product-id');
                const productName = button.getAttribute('data-product-name');
                const productPrice = button.getAttribute('data-product-price');

                modalProductId.value = productId;
                modalProductName.textContent = productName;
                modalNewPrice.value = parseFloat(productPrice).toFixed(2);
                
                priceModal.show();
            }
        });
    }

    // logica per i filtri
    const searchInput = document.getElementById('productSearchInput');
    const tableBody = document.getElementById('productTableBody');
    if (searchInput && tableBody) {
        const allRows = Array.from(tableBody.getElementsByTagName('tr'));

        searchInput.addEventListener('input', function() {
            const query = this.value.toLowerCase().trim();

            allRows.forEach(row => {
                const productNameCell = row.querySelector('.product-name');
                if (productNameCell) {
                    const productName = productNameCell.textContent.toLowerCase();
                    if (productName.includes(query)) {
                        row.style.display = '';
                    } else {
                        row.style.display = 'none';
                    }
                }
            });
        });
    }
});
</script>