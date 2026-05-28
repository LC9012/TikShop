
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp?returnUrl=assistenza.jsp");
        return;
    }
    String username = (String)session.getAttribute("username");
%>

<%-- Includiamo l'header standard --%>
<%@ include file="fragments/header.jspf" %>

<%-- Includiamo il CSS per le pagine interne --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style_pages.css">

<%-- Includiamo la navbar --%>
<%@ include file="fragments/navbar.jspf" %>

<main class="page-container">
    <div class="support-container">
        <div class="support-header">
            <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="#25F4EE" class="bi bi-headset" viewBox="0 0 16 16"><path d="M8 1a5 5 0 0 0-5 5v1h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V6a6 6 0 1 1 12 0v6a2.5 2.5 0 0 1-2.5 2.5H9.366a1 1 0 0 1-.866.5h-1a1 1 0 1 1 0-2h1a1 1 0 0 1 .866.5H11.5A1.5 1.5 0 0 0 13 12.5V6a5 5 0 0 0-5-5z"/></svg>
            <h2>Assistenza Clienti</h2>
        </div>
        <p class="support-desc">
            Se hai domande sui tuoi ordini o hai bisogno di aiuto, sei nel posto giusto.
            L'assistenza è attiva 24/7.
        </p>
        
        <div class="chat-list-header">
             <h5>Le tue conversazioni</h5>
             <button class="btn-action" id="newChatBtn" style="width: auto;">+ Nuova Chat</button>
        </div>
        
        <div class="chat-list" id="chatList">
            <div class="alert alert-info">Nessuna conversazione attiva. Inizia una nuova chat!</div>
        </div>

        <div class="text-center mt-4">
             <a href="ProductServlet" class="btn-secondary">Torna alla Home</a>
        </div>
    </div>
</main>

<!-- Modale della Chat -->
<div class="chat-modal-bg" id="chatModalBg">
    <div class="chat-modal">
        <div class="chat-modal-header">
            <h5>Chat con l'assistenza</h5>
            <button class="chat-close-btn" id="closeChatBtn">×</button>
        </div>
        <div class="chat-messages" id="chatMessages">
            <!-- I messaggi della chat verranno inseriti qui da JS -->
        </div>
        <form class="chat-input-row" id="chatForm">
            <input class="chat-input" id="chatInput" type="text" placeholder="Scrivi un messaggio..." autocomplete="off" required>
            <button class="chat-send-btn" type="submit" aria-label="Invia messaggio">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-send-fill" viewBox="0 0 16 16"><path d="M15.964.686a.5.5 0 0 0-.65-.65L.767 5.855H.766l-.452.18a.5.5 0 0 0-.082.887l.41.26.001.002 4.995 3.178 3.178 4.995.002.002.26.41a.5.5 0 0 0 .886-.083l6-15zm-1.833 1.89L6.637 10.07l-.215-.338a.5.5 0 0 0-.154-.154l-.338-.215 7.494-7.494 1.178-.471z"/></svg>
            </button>
        </form>
    </div>
</div>

<%-- Includiamo il footer standard --%>
<%@ include file="fragments/footer.jspf" %>

<%-- Includiamo lo script specifico per la pagina di assistenza --%>
<script src="<%= request.getContextPath() %>/js/assistenza.js"></script>