document.addEventListener("DOMContentLoaded", function() {

    function updateCartBadge(count) {
        const badge = document.getElementById('cartBadge');
        if (badge) { 
            if (count > 0) {
                badge.textContent = count;
                badge.style.display = 'inline-block';
            } else {
                badge.style.display = 'none';
            }
        }
    }
    window.updateCartBadge = updateCartBadge;

    function loadMiniCartContent() {
        fetch('CartServlet?mini=1')
            .then(resp => resp.text())
            .then(html => {
                const miniCartContentDiv = document.getElementById('miniCartContent');
                if (miniCartContentDiv) { 
                    miniCartContentDiv.innerHTML = html;
                }
            })
            .catch(error => console.error("Errore nel caricamento del contenuto mini-carrello:", error)); 
    }

    function loadCartBadge() {
        fetch('CartServlet?mini=1&onlyCount=1')
            .then(resp => resp.json())
            .then(data => updateCartBadge(data.count))
            .catch(error => console.error("Errore nel caricamento del badge carrello:", error)); 
    }

    // Event listeners per il mini-carrello e l'offcanvas del profilo
    const overlay = document.getElementById('miniCartOverlay');
    const cartIconBtn = document.getElementById('cartIconBtn');
    if (cartIconBtn) { 
        cartIconBtn.addEventListener('click', function() {
            loadMiniCartContent();
            if (overlay) { 
                overlay.style.display = 'block';
                setTimeout(() => overlay.classList.add('show'), 10);
            }
        });
    }
    const closeMiniCartBtn = document.getElementById('closeMiniCart');
    if (closeMiniCartBtn) { 
        closeMiniCartBtn.addEventListener('click', function() {
            if (overlay) { 
                overlay.classList.remove('show');
                setTimeout(() => overlay.style.display = 'none', 260);
            }
        });
    }
    if (overlay) { 
        overlay.addEventListener('click', function(e) {
            if (e.target === overlay) {
                overlay.classList.remove('show');
                setTimeout(() => overlay.style.display = 'none', 260);
            }
        });
    }
    loadCartBadge();

    // Logica per l'apertura dell'Offcanvas del Profilo
    const openProfileMenuBtn = document.getElementById('openProfileMenuBtn');
    const profileMenu = new bootstrap.Offcanvas(document.getElementById('profileMenu')); // Assumi Bootstrap JS è caricato

    if (openProfileMenuBtn) {
        openProfileMenuBtn.addEventListener('click', function() {
            profileMenu.show();
        });
    }


    // logica specifica per la pagina products.jsp
    const videoProducts = window.products || [];

    const allVideos = Array.from(document.querySelectorAll('.product-video'));
    if (!allVideos.length && !videoProducts.length) return;
    	
    let allProducts = videoProducts; 
    let filteredProducts = [...allProducts];
    let filteredIndexes = [...Array(videoProducts.length).keys()]; 
    let current = 0;
    let loaded = Array(allVideos.length).fill(false);

    const searchInput = document.getElementById('searchInput');
    const resetBtn = document.getElementById('resetSearchBtn');

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

    function updateProductAvgRating(productId) {
        const productRatingElement = document.getElementById('productRating');
        if (productRatingElement) {
            productRatingElement.innerHTML = renderStars(0) + '<span class="rating-value" style="margin-left:6px">0.0/5</span>';
        }
        fetch('ProductReviewsServlet?productId=' + encodeURIComponent(productId) + '&rating=ALL')
            .then(resp => resp.json())
            .then(data => {
                let avgRating = (typeof data.avgRating === "number" && !isNaN(data.avgRating)) ? data.avgRating : 0;
                if (productRatingElement) {
                    productRatingElement.innerHTML = renderStars(avgRating) +
                        '<span class="rating-value" style="margin-left:6px">' + avgRating.toFixed(1) + '/5</span>';
                }
                let recensioniCount = (typeof data.reviewsCount === "number" && !isNaN(data.reviewsCount)) ? data.reviewsCount : 0;
                const reviewsBtnElement = document.getElementById('reviewsBtn');
                if (reviewsBtnElement) { 
                    reviewsBtnElement.textContent = recensioniCount + " recensioni";
                }
            })
            .catch(error => console.error("Errore nel caricamento del rating medio:", error)); 
    }

    const reviewsBtn = document.getElementById('reviewsBtn');
    const reviewsPanelOverlay = document.getElementById('reviewsPanelOverlay');
    const closeReviewsPanelBtn = document.getElementById('closeReviewsPanelBtn');
    const reviewsPanelList = document.getElementById('reviewsPanelList');
    const reviewsPanelStarsFilter = document.getElementById('reviewsPanelStarsFilter');
    let currentPanelStars = "ALL";

    if(reviewsBtn && reviewsPanelOverlay && reviewsPanelStarsFilter) {
        reviewsBtn.addEventListener('click', function() {
            reviewsPanelOverlay.classList.add('active');
            const allRadio = reviewsPanelStarsFilter.querySelector('input[value="ALL"]');
            if (allRadio) allRadio.checked = true; 
            currentPanelStars = "ALL";
            loadReviewsPanel();
        });
        if (closeReviewsPanelBtn) { 
            closeReviewsPanelBtn.addEventListener('click', () => reviewsPanelOverlay.classList.remove('active'));
        }
    }
    
    function loadReviewsPanel() {
        if (reviewsPanelList) { 
            reviewsPanelList.innerHTML = "<div class='text-center text-secondary py-2'>Caricamento recensioni...</div>";
        }
        const cartProductIdElement = document.getElementById('cartProductId');
        const productId = cartProductIdElement ? cartProductIdElement.value : null; 
        
        if (!productId) {
            if (reviewsPanelList) reviewsPanelList.innerHTML = "<div class='alert alert-warning py-2'>ID prodotto non disponibile per le recensioni.</div>";
            return;
        }

        fetch('ProductReviewsServlet?productId=' + encodeURIComponent(productId) + '&rating=' + encodeURIComponent(currentPanelStars))
            .then(resp => {
                if (!resp.ok) {
                    throw new Error(`HTTP error! status: ${resp.status}`);
                }
                return resp.json();
            })
            .then(data => {
                const reviews = data.reviews;
                if (reviewsPanelList) {
                    if (!reviews.length) {
                        reviewsPanelList.innerHTML = `<div class="alert alert-info py-2">Nessuna recensione trovata per questo filtro.</div>`;
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
                        reviewsPanelList.innerHTML = html;
                    }
                }
            })
            .catch(error => {
                console.error("Errore nel caricamento delle recensioni:", error);
                if (reviewsPanelList) {
                    reviewsPanelList.innerHTML = `<div class="auth-message error">Errore durante il caricamento delle recensioni.</div>`;
                }
            });
    }

    function hideReviewsPanelIfOpen() {
        if (reviewsPanelOverlay && reviewsPanelOverlay.classList.contains('active')) {
            reviewsPanelOverlay.classList.remove('active');
        }
    }

    function showFilteredVideo(idx) {
        hideReviewsPanelIfOpen();
        allVideos.forEach(v => {
            v.pause();
            v.style.display = 'none';
        });
        if (!filteredProducts.length) {
            const productTitleElement = document.getElementById('productTitle');
            const productPriceElement = document.getElementById('productPrice');
            const productDescriptionElement = document.getElementById('productDescription');
            const cartProductIdElement = document.getElementById('cartProductId');
            const productRatingElement = document.getElementById('productRating');

            if (productTitleElement) productTitleElement.textContent = "";
            if (productPriceElement) productPriceElement.textContent = "";
            if (productDescriptionElement) productDescriptionElement.textContent = "Nessun prodotto trovato.";
            if (cartProductIdElement) cartProductIdElement.value = "";
            if (productRatingElement) productRatingElement.innerHTML = renderStars(0);
            return;
        }
        let idxInAll = filteredIndexes[idx];
        const v = allVideos[idxInAll];
        if (v) {
            v.style.display = '';
            if (!loaded[idxInAll]) {
                v.src = v.getAttribute('data-src');
                v.load();
                loaded[idxInAll] = true;
            }
            v.play();
        }
        const currentProduct = filteredProducts[idx];
        const productTitleElement = document.getElementById('productTitle');
        const productPriceElement = document.getElementById('productPrice');
        const productDescriptionElement = document.getElementById('productDescription');
        const cartProductIdElement = document.getElementById('cartProductId');

        if (productTitleElement) productTitleElement.textContent = currentProduct.name;
        if (productPriceElement) productPriceElement.textContent = '€ ' + Number(currentProduct.price).toFixed(2);
        if (productDescriptionElement) productDescriptionElement.textContent = currentProduct.description || "Nessuna descrizione.";
        if (cartProductIdElement) cartProductIdElement.value = currentProduct.id;
        updateProductAvgRating(currentProduct.id);
    }

    function filterProducts() {
        const query = (searchInput && searchInput.value ? searchInput.value : "").trim().toLowerCase(); 
        if (!query) {
            filteredProducts = [...allProducts];
            filteredIndexes = [...Array(videoProducts.length).keys()];
            if (resetBtn) resetBtn.classList.remove("show");
        } else {
            filteredProducts = [];
            filteredIndexes = [];
            videoProducts.forEach((p, i) => { 
                if ((p.name && p.name.toLowerCase().includes(query)) || (p.description && p.description.toLowerCase().includes(query))) {
                    filteredProducts.push(p);
                    filteredIndexes.push(i);
                }
            });
            if (resetBtn) resetBtn.classList.add("show");
        }
        current = 0;
        showFilteredVideo(current);
    }
    if (searchInput) searchInput.addEventListener("input", filterProducts);
    if (resetBtn) resetBtn.addEventListener("click", () => {
        if (searchInput) searchInput.value = ""; 
        filterProducts();
    });

    document.addEventListener('keydown', function(e) {
        if (!filteredProducts.length) return;
        if (e.key === "ArrowDown" && current < filteredProducts.length - 1) {
            current++; showFilteredVideo(current); e.preventDefault();
        } else if (e.key === "ArrowUp" && current > 0) {
            current--; showFilteredVideo(current); e.preventDefault();
        }
    });
    
    if (allVideos.length > 0) {
        showFilteredVideo(0);
    }
    
    // Logica per l'aggiunta al carrello della pagina principale
    const addToCartFormMain = document.getElementById('addToCartForm'); 
    if (addToCartFormMain) { 
        addToCartFormMain.addEventListener('submit', function(e) {
            e.preventDefault();
            const cartProductIdElement = document.getElementById('cartProductId');
            const productId = cartProductIdElement ? cartProductIdElement.value : null; 
            if (productId) {
                addToCartAjax(productId, 1);
            }
        });
    }
    
    function addToCartAjax(productId, quantity = 1) {
        fetch('CartServlet', {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'action=add&productId=' + encodeURIComponent(productId) + '&quantity=' + encodeURIComponent(quantity)
        })
        .then(resp => {
            if (!resp.ok) throw new Error('Network response was not ok');
            return resp.json();
        })
        .then(data => {
            if (data.success) {
                if (window.updateCartBadge) window.updateCartBadge(data.count); 
                const btn = document.getElementById('addCartBtn'); // Questo ID è nella pagina products.jsp
                if (btn) {
                    const originalHtml = btn.innerHTML;
                    btn.innerHTML = 'Aggiunto!';
                    btn.disabled = true;
                    setTimeout(() => {
                        btn.innerHTML = originalHtml;
                        btn.disabled = false;
                    }, 2000);
                }
            } else {
                alert(data.message || 'Errore durante l\'aggiunta al carrello!');
            }
        })
        .catch((error) => {
            console.error('Errore Fetch:', error);
            alert('Errore di rete. Impossibile aggiungere al carrello.');
        });
    }
    
    //autocompletamento barra di ricerca
    const searchInputGlobal = document.getElementById('searchInput');
    const resultsContainer = document.getElementById('autocompleteResults');
    const appContextPath = window.appContextPath || '';

    if (searchInputGlobal && resultsContainer) {
        searchInputGlobal.addEventListener('input', function() {
            const query = this.value;
            
            //dopo 2 caratteri inizia
            if (query.length < 2) { 
                resultsContainer.style.display = 'none';
                return;
            }

            fetch(`${appContextPath}/ProductSearchServlet?q=${encodeURIComponent(query)}`)
                .then(response => response.json())
                .then(data => {
                    resultsContainer.innerHTML = '';
                    
                    if (data.length > 0) {
                        data.forEach(product => {
                            const item = document.createElement('a');
                            item.className = 'autocomplete-item';
                            //link alla pagina prodotto
                            item.href = `${appContextPath}/ProductDetailsServlet?id=${product.id}`;
                            
                            const imageUrl = `${appContextPath}/${product.fotoUrl}`;
                            
                            item.innerHTML = `<img src="${imageUrl}" alt=""><span>${product.name}</span>`;
                            
                            resultsContainer.appendChild(item);
                        });
                        resultsContainer.style.display = 'block';
                    } else {
                        resultsContainer.style.display = 'none';
                    }
                })
                .catch(error => {
                    console.error('Search error:', error);
                    resultsContainer.style.display = 'none';
                });
        });

        document.addEventListener('click', function(event) {
            const searchWrapper = document.querySelector('.search-container-wrapper');
            if (searchWrapper && !searchWrapper.contains(event.target)) {
                resultsContainer.style.display = 'none';
            }
        });
    }
});