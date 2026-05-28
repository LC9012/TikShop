<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (!"admin".equals(session.getAttribute("userRole"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    Boolean success = (Boolean) request.getAttribute("success");
%>

<%@ include file="../fragments/header.jspf" %> 
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style_pages.css">
<%@ include file="../fragments/navbar.jspf" %>

<main class="page-container">
    <div class="auth-container" style="max-width: 550px;">
        <h2>Carica Nuovo Prodotto</h2>
        
        <% if (success != null && success) { %>
            <div class="upload-success-box">
                <h4>Prodotto Caricato con Successo!</h4>
                <div class="d-flex gap-3 justify-content-center">
                    <a href="upload-product.jsp" class="btn-secondary">Carica un altro</a>
                    <a href="<%= request.getContextPath() %>/AdminProductsServlet" class="btn-action">Vedi Prodotti</a>
                </div>
            </div>
        <% } else { %>
            <% if (success != null && !success) { %>
                <div class="auth-message error">
                    <%= request.getAttribute("errorMessage") %>
                </div>
            <% } %>

            <form class="auth-form" action="<%= request.getContextPath() %>/AddProductServlet" method="post" enctype="multipart/form-data">
                
                <div class="input-group">
                    <label for="name">Nome Prodotto *</label>
                    <input type="text" id="name" name="name" required maxlength="100" class="form-control-modern">
                </div>

                <div class="form-row">
                    <div class="input-group">
                        <label for="price">Prezzo (€) *</label>
                        <input type="number" step="0.01" id="price" name="price" required min="0" class="form-control-modern">
                    </div>
                    
                    <div class="input-group">
                        <label for="department">Reparto *</label>
                        <select id="department" name="department" class="form-control-modern" required>
                            <option value="">Seleziona un reparto...</option>
                            <option value="Elettronica">Elettronica</option>
                            <option value="Casa">Casa</option>
                            <option value="Giochi">Giochi</option>
                        </select>
                    </div>
                </div>

                <div class="input-group">
                    <label for="description">Descrizione</label>
                    <textarea id="description" name="description" maxlength="500" class="form-control-modern" rows="3"></textarea>
                </div>
                
                <div class="input-group">
                    <label for="video">Video Prodotto (mp4, mov) *</label>
                    <input type="file" id="video" name="video" accept="video/*" required class="form-control-modern">
                </div>
                
                <div class="input-group">
                    <label for="foto">Foto Prodotto (jpg, png) *</label>
                    <input type="file" id="foto" name="foto" accept="image/*" required class="form-control-modern">
                </div>

                <div class="mt-3">
                    <button type="submit" class="btn-action btn-lg">Carica Prodotto</button>
                </div>
            </form>
        <% } %>
    </div>
</main>

<%@ include file="../fragments/footer.jspf" %>