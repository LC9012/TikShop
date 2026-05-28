<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Includiamo l'header standard --%>
<%@ include file="fragments/header.jspf" %>

<%@ include file="fragments/navbar.jspf" %>

<%-- Includiamo il CSS per le pagine interne --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style_pages.css">

<%-- Includiamo la navbar --%>
<%@ include file="fragments/navbar.jspf" %>

<main class="page-container">
    <div class="auth-container">
        <h2>Accedi</h2>

        <%-- Messaggi di Errore o Successo --%>
        <c:if test="${not empty param.error}">
            <div class="auth-message error">
                <c:choose>
                    <c:when test="${param.error == '1'}">Credenziali non valide. Riprova.</c:when>
                    <c:otherwise>Errore del server. Riprova più tardi.</c:otherwise>
                </c:choose>
            </div>
        </c:if>
        <c:if test="${param.registered == '1'}">
            <div class="auth-message success">
                Registrazione avvenuta con successo! Ora puoi accedere.
            </div>
        </c:if>

        <form class="auth-form" method="post" action="${pageContext.request.contextPath}/LoginServlet">
            <!-- Campo nascosto per il reindirizzamento dopo il login -->
            <input type="hidden" name="returnUrl" value="${not empty param.returnUrl ? param.returnUrl : ''}">

            <div class="input-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" class="form-control-modern" required>
            </div>

            <div class="input-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control-modern" required>
            </div>

            <button type="submit" class="btn-action">Accedi</button>
        </form>

        <div class="switch-form-link">
            <span>Non hai un account? 
                <a href="<c:url value='/register.jsp'><c:if test='${not empty param.returnUrl}'><c:param name='returnUrl' value='${param.returnUrl}'/></c:if></c:url>">
                    Registrati ora
                </a>
            </span>
        </div>
    </div>
</main>

<%-- Includiamo il footer standard --%>
<%@ include file="fragments/footer.jspf" %>