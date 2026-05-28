<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Pagina Non Trovata (404) - TikShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; }
        .error-container {
            text-align: center;
            padding-top: 15vh;
        }
        .error-code {
            font-size: 8rem;
            font-weight: 700;
            color: #6c757d;
        }
        .error-message {
            font-size: 1.5rem;
            color: #343a40;
        }
    </style>
</head>
<body>
    <div class="container error-container">
        <div class="error-code">404</div>
        <h1 class="error-message">Oops! Pagina non trovata.</h1>
        <p class="lead text-muted">
            La risorsa che stai cercando potrebbe essere stata rimossa, rinominata o non è mai esistita.
        </p>
        <a href="${pageContext.request.contextPath}/ProductServlet" class="btn btn-primary btn-lg mt-4">Torna alla Home</a>
    </div>
</body>
</html>