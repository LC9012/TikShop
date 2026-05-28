document.addEventListener('DOMContentLoaded', function() {
    const products = window.productsData || [];
    const productsGrid = document.getElementById('productsGrid');
    const searchInput = document.getElementById('searchInput');
    const departmentLinks = document.querySelectorAll('.filter-link');
    
    let currentDepartment = "Tutti";
    let searchQuery = "";

    const renderProducts = () => {
        let productsToShow = [...products];

        // Filtra per reparto
        if (currentDepartment !== "Tutti") {
            productsToShow = productsToShow.filter(p => (p.department || "Altro").trim().toLowerCase() === currentDepartment.trim().toLowerCase());
        }

        // Filtra per ricerca testuale
        const query = searchQuery.trim().toLowerCase();
        if (query) {
            productsToShow = productsToShow.filter(p => 
                (p.name && p.name.toLowerCase().includes(query)) || 
                (p.description && p.description.toLowerCase().includes(query))
            );
        }

        productsGrid.innerHTML = "";
        if (!productsToShow.length) {
            productsGrid.innerHTML = `<div class="auth-message error" style="width: 100%;">Nessun prodotto trovato per i criteri selezionati.</div>`;
            return;
        }
        
        productsToShow.forEach((p, index) => {
           
            const detailUrl = `${window.appContextPath}/ProductDetailsServlet?id=${p.id}`;
            const card = document.createElement('div');
            card.className = 'product-card';
            card.style.animationDelay = `${index * 50}ms`; 
            
            card.innerHTML = `
                <a href="${detailUrl}" class="product-image-container">
                    <img class="product-image" src="${window.appContextPath}/${p.fotoUrl}" alt="Foto di ${p.name}" loading="lazy">
                </a>
                <div class="product-details">
                    <h5 class="product-name"><a href="${detailUrl}" class="text-white">${p.name}</a></h5>
                    <p class="product-price">€ ${p.price.toFixed(2)}</p>
                    <p class="product-description">${p.description || 'Nessuna descrizione disponibile.'}</p>
                    <form class="addToCartForm mt-auto" method="post" action="${window.appContextPath}/CartServlet">
                        <input type="hidden" name="productId" value="${p.id}">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="quantity" value="1">
                        <button type="submit" class="btn-action">Aggiungi al Carrello</button>
                    </form>
                </div>
            `;
            productsGrid.appendChild(card);
        });
        
        attachAddToCartEvents();
    };

    const attachAddToCartEvents = () => {
        document.querySelectorAll('.addToCartForm').forEach(form => {
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                const button = form.querySelector('button');
                const originalText = button.innerHTML;
                button.innerHTML = 'Aggiunto!';
                button.disabled = true;

                const data = new FormData(form);
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
                        // Aggiorna il badge del carrello
                        if (window.updateCartBadge) window.updateCartBadge(res.count); 
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
        });
    };

    departmentLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            departmentLinks.forEach(l => l.classList.remove('active'));
            this.classList.add('active');
            currentDepartment = this.getAttribute('data-department');
            renderProducts();
        });
    });

    searchInput.addEventListener('input', () => {
        searchQuery = searchInput.value;
        renderProducts();
    });

    renderProducts(); 
});