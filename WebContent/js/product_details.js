
document.addEventListener('DOMContentLoaded', function() {
    const productId = document.getElementById('cartProductId').value;
    if (!productId) return;

    const productRatingDetail = document.getElementById('productRatingDetail');
    const reviewsBtnDetail = document.getElementById('reviewsBtnDetail');
    const reviewsFilterDetail = document.getElementById('reviewsFilterDetail');
    const reviewsListDetail = document.getElementById('reviewsListDetail');
    const allReviewsCountSpan = document.getElementById('allReviewsCount');

    let currentReviewFilter = "ALL";

    // Funzione per renderizzare le stelle
    function renderStars(rating) {
        const max = 5;
        const rounded = Math.round((rating || 0) * 2) / 2;
        let stars = '';
        for (let i = 1; i <= max; i++) {
            if (i <= Math.floor(rounded)) stars += '★';
            else if (i - rounded === 0.5) stars += '⯨';
            else stars += '☆';
        }
        return '<span class="stars">' + stars + '</span>';
    }

    // Funzione per caricare e visualizzare le recensioni
    function loadAndDisplayReviews() {
        if (reviewsListDetail) {
            reviewsListDetail.innerHTML = "<div class='text-secondary text-center p-3'>Caricamento recensioni...</div>";
        }

        fetch(`${window.appContextPath}/ProductReviewsServlet?productId=${encodeURIComponent(productId)}&rating=${encodeURIComponent(currentReviewFilter)}`)
            .then(resp => {
                if (!resp.ok) {
                    throw new Error(`HTTP error! status: ${resp.status}`);
                }
                return resp.json();
            })
            .then(data => {
                const reviews = data.reviews;
                const avgRating = data.avgRating;
                const reviewsCount = data.reviewsCount;

                // Aggiorna il rating medio e il conteggio totale
                if (productRatingDetail) {
                    productRatingDetail.innerHTML = renderStars(avgRating) + 
                                                    `<span class="rating-value" style="margin-left:6px">${avgRating.toFixed(1)}/5</span>`;
                }
                if (reviewsBtnDetail) {
                    reviewsBtnDetail.textContent = `${reviewsCount} recensioni`;
                }
                if (allReviewsCountSpan) {
                    allReviewsCountSpan.textContent = reviewsCount;
                }

                // Renderizza la lista delle recensioni
                if (reviewsListDetail) {
                    if (!reviews.length) {
                        reviewsListDetail.innerHTML = `<div class="alert alert-info py-2">Nessuna recensione trovata per questo filtro.</div>`;
                    } else {
                        let html = "<ul class='list-group list-group-flush'>";
                        reviews.forEach(r => {
                            html += `<li class="list-group-item">
                                        <span class="review-user">${r.user}</span>
                                        <span class="review-stars">${"★".repeat(r.rating)}${"☆".repeat(5-r.rating)}</span>
                                        <div>${(r.comment ? r.comment : '<span class="text-muted">Nessun commento</span>')}</div>
                                    </li>`;
                        });
                        html += "</ul>";
                        reviewsListDetail.innerHTML = html;
                    }
                }
            })
            .catch(error => {
                console.error("Errore nel caricamento delle recensioni:", error);
                if (reviewsListDetail) {
                    reviewsListDetail.innerHTML = `<div class="auth-message error">Errore durante il caricamento delle recensioni.</div>`;
                }
            });
    }

    // Event listeners per i filtri delle recensioni
    if (reviewsFilterDetail) {
        reviewsFilterDetail.querySelectorAll('input[type="radio"]').forEach(radio => {
            radio.addEventListener('change', function() {
                currentReviewFilter = this.value;
                loadAndDisplayReviews();
            });
        });
    }

    // Gestione aggiunta al carrello
    const addToCartForm = document.getElementById('addToCartForm');
    if (addToCartForm) {
        addToCartForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const button = addToCartForm.querySelector('button');
            const originalText = button.innerHTML;
            button.innerHTML = 'Aggiunto!';
            button.disabled = true;

            const data = new FormData(addToCartForm);
            fetch(`${window.appContextPath}/CartServlet`, { 
                method: 'POST',
                headers: {'X-Requested-With': 'XMLHttpRequest'},
                body: new URLSearchParams(data)
            })
            .then(resp => {
                if (!resp.ok) throw new Error('Network response was not ok');
                return resp.json();
            })
            .then(res => {
                if (res && res.count !== undefined) {
                    if (window.updateCartBadge) window.updateCartBadge(res.count); // updateCartBadge è in products.js
                } else {
                    throw new Error("Risposta non valida dal server");
                }
            })
            .catch(err => {
                console.error("Errore aggiunta al carrello:", err);
                alert("Errore durante l'aggiunta al carrello.");
            })
            .finally(() => {
                setTimeout(() => {
                    button.innerHTML = originalText;
                    button.disabled = false;
                }, 2000);
            });
        });
    }

    // Carica le recensioni all'avvio della pagina
    loadAndDisplayReviews();
});