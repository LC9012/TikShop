<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--  l'header standard --%>
<%@ include file="fragments/header.jspf" %>

<%--  la navbar --%>
<%@ include file="fragments/navbar.jspf" %>

<%--  il CSS per le pagine interne --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style_pages.css">


<main class="page-container">
    <div class="auth-container">
        <h2>Crea il Tuo Account</h2>

        <%-- Messaggi di Errore Generali dal Server --%>
        <c:if test="${not empty param.error}">
            <div class="auth-message error">
                <c:choose>
                    <c:when test="${param.error == 'email_registrata'}">Questa email risulta già registrata.</c:when>
                    <c:otherwise>Errore del server. Riprova più tardi.</c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <form id="registerForm" class="auth-form" method="post" action="${pageContext.request.contextPath}/RegisterServlet" novalidate>
            
            <!-- Campo nascosto per il reindirizzamento -->
            <input type="hidden" name="returnUrl" value="${not empty param.returnUrl ? param.returnUrl : ''}">

            <div class="input-group">
                <label for="name">Nome</label>
                <input type="text" id="name" name="name" class="form-control-modern" required autocomplete="name" maxlength="25">
                <div id="nameError" class="input-error-message"></div>
            </div>

            <div class="input-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" class="form-control-modern" required autocomplete="email" maxlength="35">
                <div id="emailError" class="input-error-message"></div>
            </div>

            <div class="input-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control-modern" required autocomplete="new-password" maxlength="20">
                <div id="passwordError" class="input-error-message"></div>
                <div class="password-instructions">
                    Almeno 8 caratteri, con maiuscole, minuscole e numeri.
                </div>
            </div>

            <button id="submitBtn" class="btn-action" type="submit" disabled>Registrati</button>
        </form>

        <div class="switch-form-link">
            <span>Hai già un account? 
                <a href="<c:url value='/login.jsp'><c:if test='${not empty param.returnUrl}'><c:param name='returnUrl' value='${param.returnUrl}'/></c:if></c:url>">
                    Accedi
                </a>
            </span>
        </div>
    </div>
</main>

<%-- Includiamo il footer standard --%>
<%@ include file="fragments/footer.jspf" %>

<%-- Includiamo lo script di validazione specifico per questa pagina --%>
<script src="${pageContext.request.contextPath}/js/register.js"></script>