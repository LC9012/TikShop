<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%-- Includiamo l'header standard --%>
<%@ include file="fragments/header.jspf" %>

<%-- Includiamo il CSS per le pagine interne --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style_pages.css">

<%-- Includiamo la navbar --%>
<%@ include file="fragments/navbar.jspf" %>

<main class="page-container">
    <div class="privacy-container">
        <h2>Informativa sulla Privacy</h2>
        <p>
            La tua privacy è fondamentale per noi di TikShop. Questa informativa descrive come raccogliamo, utilizziamo e proteggiamo i tuoi dati personali in conformità con il Regolamento Generale sulla Protezione dei Dati (GDPR) e le normative vigenti.
        </p>
        
        <h4>Punti Chiave del Trattamento dei Dati</h4>
        <ul>
            <li>
                <strong>Finalità dei Dati:</strong> I tuoi dati personali (nome, email, indirizzo) sono utilizzati esclusivamente per la gestione dei tuoi ordini, per fornire assistenza e per le funzionalità essenziali del sito, come il login e la gestione del profilo.
            </li>
            <li>
                <strong>Cancellazione Account:</strong> Hai il pieno controllo sui tuoi dati. Puoi eliminare il tuo account in qualsiasi momento dalla pagina <a href="impostazioni.jsp">Impostazioni</a>. Questa azione cancellerà in modo permanente i tuoi dati personali dai nostri sistemi attivi.
            </li>
            <li>
                <strong>Nessuna Cessione a Terzi:</strong> Ci impegniamo a non vendere, affittare o cedere i tuoi dati personali a società terze per scopi di marketing o commerciali.
            </li>
            <li>
                <strong>Contatti:</strong> Per qualsiasi domanda, dubbio o per esercitare i tuoi diritti sulla privacy, puoi contattare il nostro Responsabile della Protezione dei Dati all'indirizzo email: <a href="mailto:support@tikshop.com">support@tikshop.com</a>.
            </li>
        </ul>

        <div class="text-center mt-4">
            <a href="javascript:history.back()" class="btn-secondary">Torna Indietro</a>
        </div>
    </div>
</main>

<%-- Includiamo il footer standard --%>
<%@ include file="fragments/footer.jspf" %>