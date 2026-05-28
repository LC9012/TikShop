<%-- File: /impostazioni.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp?returnUrl=impostazioni.jsp");
        return;
    }
%>

<%-- Includiamo l'header standard --%>
<%@ include file="fragments/header.jspf" %>

<%-- Includiamo il CSS per le pagine interne --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style_pages.css">

<%-- Includiamo la navbar --%>
<%@ include file="fragments/navbar.jspf" %>

<main class="page-container">
    <div class="settings-container">
        <h2>Impostazioni</h2>

        <!-- RIGA ELIMINA ACCOUNT -->
        <div class="setting-row">
            <div class="setting-label">
                <strong>Elimina Account</strong>
                <p class="text-white small mb-0">Questa azione è permanente e irreversibile.</p>
            </div>
            <form method="post" action="DeleteAccountServlet" onsubmit="return confirm('Sei DAVVERO sicuro di voler eliminare il tuo account? Tutti i tuoi ordini e recensioni saranno cancellati per sempre!');" style="margin: 0;">
                <button class="btn-danger" type="submit" style="width: auto; padding: 0.5em 1.2em;">Elimina</button>
            </form>
        </div>
        
        <div class="setting-row">
  <span class="setting-label"><strong>Tema</strong></span>
  <label class="theme-switch">
    <input type="checkbox" id="" onchange="">
    <span class="slider">
      <span class="icon icon-moon">scuro</span>
      <span class="icon icon-sun">chiaro</span>
    </span>
  </label>
</div>
        
        
        <!-- RIGA PRIVACY  -->
        <div class="info-row">
            <p class="mb-0">Leggi la nostra <a class="privacy-link" href="privacy.jsp">informativa sulla privacy</a> per sapere come gestiamo i tuoi dati.</p>
        </div>
        
        <div class="text-center mt-4">
            <a href="ProductServlet" class="btn-secondary">Torna alla Home</a>
        </div>
        
        <div class="site-version">
            Versione sito: 1.0.0
        </div>
    </div>
</main>

<%-- Includiamo il footer standard --%>
<%@ include file="fragments/footer.jspf" %>