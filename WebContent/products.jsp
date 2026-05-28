<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.Product" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--  l'header --%>
<%@ include file="fragments/header.jspf" %>

<%--  la navbar --%>
<%@ include file="fragments/navbar.jspf" %>



<%-- Alert di benvenuto dopo la registrazione --%>
<% if ("success".equals(request.getParameter("registration"))) { %>
    <div id="welcomeAlert" class="alert alert-success alert-dismissible fade show" role="alert" style="position: fixed; top: 80px; right: 20px; z-index: 9999; min-width: 250px;">
        <strong>Benvenuto!</strong> Registrazione avvenuta con successo.
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const welcomeAlert = document.getElementById('welcomeAlert');
            if (welcomeAlert) { setTimeout(() => new bootstrap.Alert(welcomeAlert).close(), 4000); }
        });
    </script>
<% } %>

<%-- Layout principale a 3 colonne --%>
<div class="main-flex-row">
  <!-- Sidebar sinistra -->
  <div class="left-sidebar">
    <div class="profile-btn-box">
      <button class="profile-btn" id="openProfileMenuBtn">Profilo</button>
    </div>
    <div class="logo-tikshop">TikShop</div>
    <div class="slogan-tikshop">"piattaforma rivoluzionaria per gli acquisti online"</div>
  </div>
  
  <!-- Colonna centrale video -->
  <div class="video-center-col">
    <div class="video-search-bar search-container-wrapper">
    <input type="text" id="searchInput" class="video-search-input" placeholder="Cerca prodotto o descrizione...">
    <button class="search-reset-btn" id="resetSearchBtn" title="Torna a tutti i prodotti">←</button>
    
    <div id="autocompleteResults" class="autocomplete-results"></div>
</div>
    <div class="video-box-area">
      <%
        List<Product> products = (List<Product>) request.getAttribute("products");
        boolean hasProducts = products != null && !products.isEmpty();
        if (hasProducts) {
           int idx = 0;
           for (Product p : products) {
             String videoSrc = (p.getVideoUrl() != null && !p.getVideoUrl().isEmpty()) ? request.getContextPath() + "/" + p.getVideoUrl() : "";
      %>
        <video class="tiktok-video product-video" id="video_<%=idx%>" src="<%= videoSrc %>" data-src="<%= videoSrc %>" preload="auto" loop muted poster="default.jpg" tabindex="0" <%= idx==0 ? "" : "style=\"display:none;\"" %> controls></video>
      <%
           idx++;
           }
      %>
      <div class="arrow-hint">Usa <b>Freccia Su</b> / <b>Freccia Giù</b> per cambiare video</div>
      <% } else { %>
      <div class="alert alert-warning" style="margin-top:40px;">Nessun prodotto disponibile.</div>
      <% } %>
    </div>
  </div>
  
  <!-- Colonna destra info prodotto -->
  <div class="right-info-col">
    <div class="cart-btn-box">
      <button id="cartIconBtn" class="cart-icon-btn" aria-label="Vai al carrello">
        <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="#397be5" class="bi bi-cart3" viewBox="0 0 16 16"><path d="M0 1.5A.5.5 0 0 1 .5 1h1a.5.5 0 0 1 .485.379L2.89 5H14.5a.5.5 0 0 1 .49.598l-1.5 7A.5.5 0 0 1 13 13H4a.5.5 0 0 1-.491-.408L1.01 2H.5a.5.5 0 0 1-.5-.5zM3.102 6l1.313 6h8.17l1.313-6H3.102z"/><circle cx="6" cy="14" r="1"/><circle cx="11" cy="14" r="1"/></svg>
        <span id="cartBadge" class="cart-badge" style="display:none;">0</span>
      </button>
    </div>
    <div class="product-info-panel" style="position:relative;">
      <div class="product-title" id="productTitle"></div>
      <div class="product-price" id="productPrice"></div>
      <div class="product-description" id="productDescription"></div>
      <div class="product-rating" id="productRating"></div>
      <div class="reviews-count" id="reviewsBtn" tabindex="0">0 recensioni</div>
      <div id="reviewsPanelOverlay">
          <div id="reviewsPanel" class="position-relative">
              <button id="closeReviewsPanelBtn" aria-label="Chiudi">×</button>
              <div id="reviewsPanelTitle">Recensioni</div>
              <div id="reviewsPanelStarsFilter" class="mb-2">
                  <label><input type="radio" name="panelStars" value="ALL" checked> Tutte</label>
                  <label><input type="radio" name="panelStars" value="5">5★</label>
                  <label><input type="radio" name="panelStars" value="4">4★</label>
                  <label><input type="radio" name="panelStars" value="3">3★</label>
                  <label><input type="radio" name="panelStars" value="2">2★</label>
                  <label><input type="radio" name="panelStars" value="1">1★</label>
              </div>
              <div id="reviewsPanelList"></div>
          </div>
      </div>
      <form id="addToCartForm" class="product-add-cart-form" method="post" action="<%= request.getContextPath() %>/CartServlet">
        <input type="hidden" name="productId" id="cartProductId" value="">
        <input type="hidden" name="quantity" value="1">
        <button type="submit" class="product-add-cart-btn" id="addCartBtn">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="#fff" class="bi bi-cart3" viewBox="0 0 16 16" style="vertical-align:middle;margin-right:5px;"><path d="M0 1.5A.5.5 0 0 1 .5 1h1a.5.5 0 0 1 .485.379L2.89 5H14.5a.5.5 0 0 1 .49.598l-1.5 7A.5.5 0 0 1 13 13H4a.5.5 0 0 1-.491-.408L1.01 2H.5a.5.5 0 0 1-.5-.5zM3.102 6l1.313 6h8.17l1.313-6H3.102z"/><circle cx="6" cy="14" r="1"/><circle cx="11" cy="14" r="1"/></svg>
          Aggiungi al carrello
        </button>
      </form>
    </div>

    <div class="affianco-slogan-tradizionale">
      <div class="slogan-tradizionale-style">
        ma abbiamo pensato anche a te che preferisci gli acquisti tradizionali
      </div>
      <c:url value="/ProductServlet" var="traditionalShopUrl">
          <c:param name="view" value="traditional"/>
      </c:url>
      <button class="pendulum-btn" onclick="window.location.href='${traditionalShopUrl}'">
          vai all'<br>ecommerce<br>tradizionale
      </button>
    </div>
  </div>
</div>

<!-- Mini carrello -->
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

<!-- Offcanvas del Profilo con Icone  -->
<div class="offcanvas offcanvas-start" tabindex="-1" id="profileMenu" aria-labelledby="profileMenuLabel">
  <div class="offcanvas-header">
    <h5 class="offcanvas-title" id="profileMenuLabel">Account</h5>
    <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Chiudi"></button>
  </div>
  <div class="offcanvas-body">
    <ul class="list-group list-group-flush mb-4">
      <%
        String userRole = (String) session.getAttribute("userRole");
        String username = (String) session.getAttribute("username");
        boolean isLoggedIn = username != null;

        if (isLoggedIn) {
          if ("admin".equals(userRole)) {
      %>
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
      <%
          } else {
      %>
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
      <%
          }
        }
      %>
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

<%-- 4. Includi il footer --%>
<%@ include file="fragments/footer.jspf" %>