<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="control.ProductDetailsServlet.ProductDetails, java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    ProductDetails product = (ProductDetails) request.getAttribute("product");
    if (product == null) {
        response.sendRedirect("ProductServlet");
        return;
    }
    String videoSrc = (product.getVideoUrl() != null && !product.getVideoUrl().isEmpty()) ? request.getContextPath() + "/" + product.getVideoUrl() : "";
    String fotoSrc = (product.getFotoUrl() != null && !product.getFotoUrl().isEmpty()) ? request.getContextPath() + "/" + product.getFotoUrl() : "";
    String userRole = (String) session.getAttribute("userRole");
    String username = (String) session.getAttribute("username");
    boolean isLoggedIn = username != null;
%>

<%-- Standard header and navbar --%>
<%@ include file="fragments/header.jspf" %>
<%@ include file="fragments/navbar.jspf" %>

<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style_pages.css">

<style>
    .product-detail-container {
        max-width: 1200px;
        margin: 2rem auto;
        padding: 2rem;
        background: #2a2f3c;
        border-radius: 16px;
        color: #f1f1f1;
    }

    .product-header-section {
        text-align: center;
        margin-bottom: 2.5rem;
        border-bottom: 1px solid #4a5060;
        padding-bottom: 1.5rem;
    }

    .product-title-detail {
        font-size: 2.8rem;
        font-weight: 700;
        color: #fff;
        margin-bottom: 0.5rem;
    }

    .product-price-detail {
        font-size: 2rem;
        font-weight: 300;
        color: #a8e5c1;
        margin-bottom: 1rem;
    }
    
    .product-rating-detail-container {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 10px;
        font-size: 1.1rem;
    }

    .product-media-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 2rem;
        align-items: flex-start;
        margin-bottom: 2.5rem;
    }

    .product-main-image {
        width: 100%;
        border-radius: 12px;
        box-shadow: 0 8px 25px rgba(0,0,0,0.3);
    }

    .product-video-detail {
        width: 100%;
        border-radius: 12px;
    }
    
    .product-actions-description {
        display: flex;
        flex-direction: column;
        justify-content: center;
        height: 100%;
    }

    .product-description-detail {
        font-size: 1.1rem;
        line-height: 1.6;
        color: #dcdcdc;
        margin-bottom: 2rem;
    }

    .reviews-section {
        margin-top: 3rem;
        padding-top: 2rem;
        border-top: 1px solid #4a5060;
    }

    .reviews-section-title {
        font-size: 1.8rem;
        margin-bottom: 1.5rem;
        color: #fff;
    }
</style>



<div class="page-container">
    <div class="product-detail-container">

        <div class="product-header-section">
            <h1 class="product-title-detail"><%= product.getName() %></h1>
            <p class="product-price-detail">€ <%= String.format("%.2f", product.getPrice()) %></p>
            <div class="product-rating-detail-container">
                 <div class="product-rating-detail" id="productRatingDetail"></div>
                 <span class="reviews-count-detail" id="reviewsBtnDetail">0 recensioni</span>
            </div>
        </div>

        <div class="product-media-grid">
            <div class="product-photo-column">
                <c:if test="${!empty product.fotoUrl}">
                    <img src="<%= fotoSrc %>" alt="<%= product.getName() %>" class="product-main-image">
                </c:if>
            </div>
            
            <div class="product-video-column">
                <video class="product-video-detail" controls loop muted poster="default.jpg">
                    <source src="<%= videoSrc %>" type="video/mp4">
                    Your browser does not support the video tag.
                </video>
            </div>
        </div>

        <div class="product-actions-description">
            <p class="product-description-detail"><%= product.getDescription() %></p>
            
            <form id="addToCartForm" method="post" action="<%= request.getContextPath() %>/CartServlet">
                <input type="hidden" name="productId" id="cartProductId" value="<%= product.getId() %>">
                <input type="hidden" name="quantity" value="1">
            </form>
        </div>

        <div class="reviews-section">
            <h3 class="reviews-section-title">Tutte le recensioni (<span id="allReviewsCount">0</span>)</h3>
            <div class="reviews-filter-detail" id="reviewsFilterDetail">
                <label><input type="radio" name="panelStarsDetail" value="ALL" checked> Tutte</label>
                <label><input type="radio" name="panelStarsDetail" value="5">5★</label>
                <label><input type="radio" name="panelStarsDetail" value="4">4★</label>
                <label><input type="radio" name="panelStarsDetail" value="3">3★</label>
                <label><input type="radio" name="panelStarsDetail" value="2">2★</label>
                <label><input type="radio" name="panelStarsDetail" value="1">1★</label>
            </div>
            <div class="reviews-list-detail" id="reviewsListDetail">
                </div>
        </div>
    </div>
</div>


<%@ include file="fragments/footer.jspf" %>

<script>
    window.appContextPath = "<%= request.getContextPath() %>";
</script>
<script src="<%= request.getContextPath() %>/js/products.js"></script>
<script src="<%= request.getContextPath() %>/js/product_details.js"></script>