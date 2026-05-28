<%@ page import="java.sql.*, util.DBUtil" %>
<%@ page session="true" %>

<%
    HttpSession s = request.getSession(false);
    if (s == null || s.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp?returnUrl=profilo.jsp");
        return;
    }
    String nomeUtente = (String) s.getAttribute("username");
    String emailUtente = "Non disponibile";
    int userId = (Integer) s.getAttribute("userId");
    try (Connection conn = DBUtil.getConnection()) {
        String sql = "SELECT email FROM users WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                emailUtente = rs.getString("email");
            }
        }
    } catch (Exception e) { e.printStackTrace(); }

    String success = request.getParameter("success");
    String error = request.getParameter("error");
    String errorMsg = "";
    if ("campi_obbligatori".equals(error)) {
        errorMsg = "Compila tutti i campi obbligatori.";
    } else if (!"".equals(error) && error != null) {
        errorMsg = "Errore durante l'aggiornamento dei dati.";
    }
%>

<%-- Includiamo l'header standard --%>
<%@ include file="fragments/header.jspf" %>

<%-- Includiamo il nuovo CSS per le pagine interne --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style_pages.css">

<%-- Includiamo la navbar --%>
<%@ include file="fragments/navbar.jspf" %>

<main class="page-container">
    <div class="profile-card-container">
        <h2>Il Mio Profilo</h2>

        <%-- Messaggi di Avviso --%>
        <% if (success != null) { %>
            <div class="alert-modern alert-success">Dati aggiornati con successo!</div>
        <% } else if (errorMsg != null && !"".equals(errorMsg)) { %>
            <div class="alert-modern alert-danger"><%= errorMsg %></div>
        <% } %>

        <!-- Visualizzazione Dati -->
        <div id="show-data" class="profile-data-display">
            <div class="mb-3">
                <strong>Nome:</strong> <%= nomeUtente %>
            </div>
            <div class="mb-3">
                <strong>Email:</strong> <%= emailUtente %>
            </div>
            
            <button class="btn-action mt-4" onclick="showEditForm()">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" class="bi bi-pencil-square" viewBox="0 0 16 16"><path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/><path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/></svg>
                Modifica Dati
            </button>
            
            <div class="d-flex gap-3 mt-3">
                <a href="ProductServlet" class="btn-secondary">Torna alla Home</a>
                <form action="LogoutServlet" method="post" style="width:100%; margin:0;">
                    <button type="submit" class="btn-danger">Logout</button>
                </form>
            </div>
        </div>

        <!-- Form Modifica Dati  -->
        <div id="edit-data" style="display:none;">
            <div class="edit-form-container">
                <form method="post" action="UpdateProfileServlet">
                    <div class="mb-3">
                        <label for="newName" class="form-label">Nuovo nome</label>
                        <input type="text" name="name" id="newName" value="<%= nomeUtente %>" class="form-control form-control-modern" required>
                    </div>
                    <div class="mb-3">
                        <label for="newEmail" class="form-label">Nuova email</label>
                        <input type="email" name="email" id="newEmail" value="<%= emailUtente %>" class="form-control form-control-modern" required>
                    </div>
                    <div class="mb-3">
                        <label for="newPassword" class="form-label">Nuova password</label>
                        <input type="password" name="password" id="newPassword" class="form-control form-control-modern" autocomplete="new-password">
                        <div class="form-text mt-1">Lascia vuoto se non vuoi cambiare la password.</div>
                    </div>
                    <div class="d-flex gap-3 mt-4">
                        <button type="submit" class="btn-action">Salva Modifiche</button>
                        <button type="button" class="btn-secondary" onclick="hideEditForm()">Annulla</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</main>

<%-- Includiamo il footer standard --%>
<%@ include file="fragments/footer.jspf" %>


<script>
    function showEditForm() {
        document.getElementById('show-data').style.display = 'none';
        document.getElementById('edit-data').style.display = 'block';
    }
    function hideEditForm() {
        document.getElementById('show-data').style.display = 'block';
        document.getElementById('edit-data').style.display = 'none';
    }
</script>