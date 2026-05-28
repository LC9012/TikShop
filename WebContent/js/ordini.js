document.addEventListener('DOMContentLoaded', function() {
    const reviewModalElement = document.getElementById('reviewModal');
    if (!reviewModalElement) return;

    // Creiamo una singola istanza del modale di Bootstrap che possiamo riutilizzare
    const reviewModal = new bootstrap.Modal(reviewModalElement);

    // Selezioniamo tutti i pulsanti per le recensioni
    const reviewButtons = document.querySelectorAll('.btn-review');

    // Aggiungiamo un listener a ogni pulsante
    reviewButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Recupera i dati dal pulsante cliccato
            const productId = this.dataset.productId;
            const productName = this.dataset.productName;

            // Popola i campi del modale con i dati del prodotto
            reviewModalElement.querySelector('#reviewProductName').textContent = productName;
            reviewModalElement.querySelector('#reviewProductId').value = productId;
            reviewModalElement.querySelector('#reviewComment').value = '';
            reviewModalElement.querySelector('#reviewRating').value = '0';
            
            // Resetta le stelle al loro stato iniziale
            const allStars = reviewModalElement.querySelectorAll('.star');
            allStars.forEach(s => {
                s.innerHTML = '☆';
                s.classList.remove('selected');
            });

            // Mostra il modale usando il nostro oggetto JS
            reviewModal.show();
        });
    });

    // Gestione delle stelle per il rating
    const allStars = reviewModalElement.querySelectorAll('.star');
    const ratingInput = reviewModalElement.querySelector('#reviewRating');

    allStars.forEach(star => {
        star.addEventListener('click', function () {
            const value = this.getAttribute('data-value');
            ratingInput.value = value;
            allStars.forEach(s => {
                const sValue = s.getAttribute('data-value');
                s.innerHTML = sValue <= value ? '★' : '☆';
                s.classList.toggle('selected', sValue <= value);
            });
        });

        star.addEventListener('mouseover', function () {
            const value = this.getAttribute('data-value');
            allStars.forEach(s => {
                if (s.getAttribute('data-value') <= value) {
                    s.classList.add('hovered');
                }
            });
        });

        star.addEventListener('mouseout', function () {
            allStars.forEach(s => s.classList.remove('hovered'));
        });
    });

    // Gestione del submit del form
    const reviewForm = document.getElementById('reviewForm');
    if (reviewForm) {
        reviewForm.addEventListener('submit', function (e) {
            e.preventDefault();

            if (ratingInput.value === '0') {
                alert("Per favore, seleziona un voto da 1 a 5 stelle.");
                return;
            }
            
            const formAction = this.getAttribute('action');
            const formData = new URLSearchParams(new FormData(this)).toString();

            fetch(formAction, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    window.location.href = window.appContextPath + '/ProductServlet?review=success';
                } else {
                    alert(data.message || 'Si è verificato un errore.');
                }
            })
            .catch(error => {
                console.error('Errore Fetch:', error);
                alert('Impossibile inviare la recensione. Controlla la console per i dettagli.');
            });
        });
    }
});