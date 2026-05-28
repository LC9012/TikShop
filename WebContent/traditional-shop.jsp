<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Product, java.util.List, java.util.ArrayList" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<%
    // Recupero dei dati passati dal servlet
    List<Product> products = (List<Product>) request.getAttribute("traditionalProducts");
    if (products == null) {
        products = new ArrayList<>(); // Inizializza una lista vuota per evitare NullPointerException
    }
    pageContext.setAttribute("products", products);

    List<String> departments = (List<String>) request.getAttribute("departments");
    if (departments == null) {
        departments = new ArrayList<>();
    }
    pageContext.setAttribute("departments", departments);

    // Dati per l'Offcanvas del Profilo
    String userRole = (String) session.getAttribute("userRole");
    String username = (String) session.getAttribute("username");
    boolean isLoggedIn = username != null;
%>

<%-- Includiamo l'header standard --%>
<%@ include file="fragments/header.jspf" %>

<%-- Includiamo il CSS per lo shop tradizionale --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/traditional_shop.css">


<%-- Includiamo la navbar --%>
<%@ include file="fragments/navbar.jspf" %>

<div class="profile-btn-fixed">
    <button class="profile-btn" id="openProfileMenuBtn">Profilo</button>
</div>

<main class="page-container-traditional">

    <aside class="sidebar-filters">
        <h3 class="filters-title">Reparti</h3>
        <ul class="filter-list" id="departmentList">
            <li><a href="#" class="filter-link active" data-department="Tutti">Tutti</a></li>
            <c:forEach var="dept" items="${departments}">
                <li><a href="#" class="filter-link" data-department="${dept}">${dept}</a></li>
            </c:forEach>
        </ul>
    </aside>
    
    <div class="catalog-container">
        <header class="catalog-header search-container-wrapper">
    <input type="text" id="searchInput" class="form-control-modern" placeholder="Cerca un prodotto...">

    <div id="autocompleteResults" class="autocomplete-results"></div>
</header>
        <div class="products-grid" id="productsGrid">
            <%-- I prodotti verranno renderizzati qui da JavaScript --%>
        </div>
    </div>
</main>

<div class="cart-btn-box-fixed">
    <button id="cartIconBtn" class="cart-icon-btn" aria-label="Vai al carrello">
        <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="#397be5" class="bi bi-cart3" viewBox="0 0 16 16"><path d="M0 1.5A.5.5 0 0 1 .5 1h1a.5.5 0 0 1 .485.379L2.89 5H14.5a.5.5 0 0 1 .49.598l-1.5 7A.5.5 0 0 1 13 13H4a.5.5 0 0 1-.491-.408L1.01 2H.5a.5.5 0 0 1-.5-.5zM3.102 6l1.313 6h8.17l1.313-6H3.102z"/><circle cx="6" cy="14" r="1"/><circle cx="11" cy="14" r="1"/></svg>
        <span id="cartBadge" class="cart-badge" style="display:none;">0</span>
    </button>
</div>

<div id="miniCartOverlay" class="mini-cart-overlay">
    <div class="mini-cart-panel">
        <button class="close-mini-cart" id="closeMiniCart">×</button>
        <h5 class="mb-3">Il tuo carrello</h5>
        <div id="miniCartContent">Caricamento...</div>
        <div class="d-grid mt-3">
            <a href="<%= request.getContextPath() %>/CartServlet" class="btn btn-primary">Vai al carrello</a>
        </div>
    </div>
</div>

<div class="offcanvas offcanvas-start" tabindex="-1" id="profileMenu" aria-labelledby="profileMenuLabel">
    <div class="offcanvas-header">
        <h5 class="offcanvas-title" id="profileMenuLabel">Account</h5>
        <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Chiudi"></button>
    </div>
    <div class="offcanvas-body">
        <ul class="list-group list-group-flush mb-4">
            <% if (isLoggedIn) { %>
                <% if ("admin".equals(userRole)) { %>
                    <li class="list-group-item list-group-item-dark"><strong>Pannello Admin</strong></li>
                    <li class="list-group-item">
                        <a href="<%= request.getContextPath() %>/AdminProductsServlet" class="offcanvas-link">
                            <span class="material-icons">inventory_2</span> Gestione Prodotti
                        </a>
                    </li>
                    <li class="list-group-item">
                        <a href="<%= request.getContextPath() %>/AdminOrdersServlet" class="offcanvas-link">
                            <span class="material-icons">receipt_long</span> Visualizza Ordini
                        </a>
                    </li>
                    <li class="list-group-item">
                        <a href="<%= request.getContextPath() %>/admin/upload-product.jsp" class="offcanvas-link">
                            <span class="material-icons">add_box</span> Aggiungi Prodotto
                        </a>
                    </li>
                <% } else { %>
                    <li class="list-group-item">
                        <a href="<%= request.getContextPath() %>/profilo.jsp" class="offcanvas-link">
                            <span class="material-icons">account_circle</span> Il mio profilo
                        </a>
                    </li>
                    <li class="list-group-item">
                        <a href="<%= request.getContextPath() %>/OrdiniServlet" class="offcanvas-link">
                            <span class="material-icons">shopping_bag</span> I miei ordini
                        </a>
                    </li>
                    <li class="list-group-item">
                        <a href="<%= request.getContextPath() %>/assistenza.jsp" class="offcanvas-link">
                            <span class="material-icons">support_agent</span> Assistenza
                        </a>
                    </li>
                    <li class="list-group-item">
                        <a href="<%= request.getContextPath() %>/impostazioni.jsp" class="offcanvas-link">
                            <span class="material-icons">settings</span> Impostazioni
                        </a>
                    </li>
                <% } %>
            <% } %>
        </ul>
        
        <% if (isLoggedIn) { %>
            <form method="post" action="<%= request.getContextPath() %>/LogoutServlet" class="d-grid">
                <button type="submit" class="btn btn-danger">
                    <span class="material-icons">logout</span> Logout
                </button>
            </form>
        <% } else { %>
            <div class="d-grid">
                <a href="<%= request.getContextPath() %>/login.jsp" class="btn btn-action">
                    <span class="material-icons">login</span> Accedi / Registrati
                </a>
            </div>
        <% } %>
    </div>
</div>

<%-- Includiamo il footer standard --%>
<%@ include file="fragments/footer.jspf" %>

<script>
    window.productsData = [
        <% for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            
            String name = (p.getName() != null) ? p.getName().replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "\\n") : "";
            String fotoUrl = (p.getFotoUrl() != null) ? p.getFotoUrl().replace("\\", "\\\\").replace("\"", "\\\"") : "";
            String description = (p.getDescription() != null) ? p.getDescription().replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "\\n") : "Nessuna descrizione disponibile.";
            String department = (p.getDepartment() != null && !p.getDepartment().isEmpty()) ? p.getDepartment().replace("\"", "\\\"") : "Altro";
        %>
        {
            "id": <%= p.getId() %>,
            "name": "<%= name %>",
            "fotoUrl": "<%= fotoUrl %>", 
            "price": <%= p.getPrice() %>,
            "description": "<%= description %>",
            "rating": <%= p.getRating() %>,
            "department": "<%= department %>"
        }
        <%= (i < products.size() - 1) ? "," : "" %> 
        <% } %>
    ];

    // Definiamo anche il contextPath per essere sicuri
    window.appContextPath = "<%= request.getContextPath() %>";
</script>

<%-- Includiamo lo script specifico per questa pagina --%>
<script src="<%= request.getContextPath() %>/js/traditional_shop.js"></script>
