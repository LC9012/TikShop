-- =======================================================
-- SCHEMA DI INIZIALIZZAZIONE DATABASE PER TIKSHOP
-- =======================================================

CREATE DATABASE IF NOT EXISTS tikshop;
USE tikshop;

-- 1. Tabella Utenti (users)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Contiene l'hash BCrypt della password
    role VARCHAR(20) DEFAULT 'customer'
);

-- 2. Tabella Prodotti (products)
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    foto VARCHAR(255), -- Percorso relativo dell'immagine (es. uploads/Foto/Aspirapolvere.jpg)
    video_url VARCHAR(255), -- Percorso relativo del video (es. uploads/Video/Aspirapolvere.mp4)
    department VARCHAR(100), -- Reparto (es. Elettronica, Casa, Giochi)
    rating INT DEFAULT 0, -- Valutazione media complessiva (0-5)
    is_active BOOLEAN DEFAULT TRUE
);

-- 3. Tabella Carrello (cart)
CREATE TABLE IF NOT EXISTS cart (
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 4. Tabella Recensioni (reviews)
CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 5. Tabella Ordini (orders)
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'Pending',
    shipping_address TEXT NOT NULL,
    card_holder_name VARCHAR(150) NOT NULL,
    encrypted_card_number VARCHAR(255) NOT NULL, -- Dettagli carta cifrati per sicurezza
    card_last_four_digits VARCHAR(4) NOT NULL,
    card_expiry_date VARCHAR(10) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. Tabella Articoli Ordine (order_items)
CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- =======================================================
-- POPOLAMENTO DATI SEED INIZIALI (PRODOTTI E UTENTI)
-- =======================================================

-- Utenti di prova (La password per entrambi è "password123")
-- Hash generato con BCrypt
INSERT INTO users (name, email, password, role) VALUES 
('Admin TikShop', 'admin@tikshop.com', '$2a$10$7Z25Qv5o69nB94d3Qj17/euV/c.e7XF9pW.wXJ6Ua.lqH.jA7vIe6', 'admin'),
('Mario Rossi', 'mario.rossi@gmail.com', '$2a$10$7Z25Qv5o69nB94d3Qj17/euV/c.e7XF9pW.wXJ6Ua.lqH.jA7vIe6', 'customer');

-- Prodotti corrispondenti alle immagini e ai video presenti nella cartella uploads
INSERT INTO products (name, description, price, foto, video_url, department, rating, is_active) VALUES
('Aspirapolvere Robot', 'Aspirapolvere intelligente senza fili, programmabile con app mobile e sensori anti-ostacolo.', 199.99, 'uploads/Foto/Aspirapolvere.jpg', 'uploads/Video/Aspirapolvere.mp4', 'Elettrodomestici', 4, TRUE),
('Bambola Interattiva', 'Bambola parlante con espressioni facciali realistiche, perfetta per i più piccoli.', 34.50, 'uploads/Foto/Bambola.jpg', 'uploads/Video/BambolaInterattiva.mp4', 'Giocattoli', 5, TRUE),
('Console Retrogaming', 'Mini console precaricata con oltre 10000 giochi classici degli anni 80 e 90, include 2 controller wireless.', 59.99, 'uploads/Foto/ConsoleRetro.png', 'uploads/Video/ConsoleRetro.mp4', 'Elettronica', 4, TRUE),
('Cuffie Gaming PRO', 'Cuffie stereo wireless con microfono a cancellazione attiva del rumore e luci RGB.', 79.90, 'uploads/Foto/CuffieGaming.jpg', 'uploads/Video/CuffieGaming.mp4', 'Elettronica', 5, TRUE),
('Ferro da Stiro a Vapore', 'Ferro da stiro ultra leggero con piastra in ceramica e colpo di vapore ad alta pressione.', 29.99, 'uploads/Foto/FerroVapore.jpg', 'uploads/Video/FerroVapore.mp4', 'Elettrodomestici', 3, TRUE),
('Friggitrice ad Aria XXL', 'Cottura ad aria calda fino a 200°C per fritture croccanti senza olio, cestello da 5.5 litri.', 119.00, 'uploads/Foto/FriggitriceAria.jpg', 'uploads/Video/FriggitriceAria.mp4', 'Elettrodomestici', 5, TRUE),
('Gioco da Tavolo Fantasy', 'Gioco di strategia cooperativo ambientato in un mondo fantasy medievale, da 2 a 5 giocatori.', 39.99, 'uploads/Foto/GiocoDaTavolo.jpg', 'uploads/Video/GiocoDaTavolo.mp4', 'Giocattoli', 4, TRUE),
('Lampada a LED Smart', 'Lampada da scrivania programmabile con intensità dimmerabile e colori RGB regolabili da smartphone.', 24.99, 'uploads/Foto/LampadaLED.jpg', 'uploads/Video/LampadaLED.mp4', 'Casa', 4, TRUE),
('Macchina del Caffè Espresso', 'Macchina espresso manuale compatibile con cialde e polvere, dotata di cappuccinatore orientabile.', 89.90, 'uploads/Foto/MacchinaCaffe.jpg', 'uploads/Video/MacchinaCaffe.mp4', 'Elettrodomestici', 4, TRUE),
('Macchina Fotografica Vintage', 'Fotocamera digitale compatta con design retrò e sensore ad alta definizione da 24 Megapixel.', 149.00, 'uploads/Foto/MacchinaFotografica.jpg', 'uploads/Video/MacchinaFotografica.mp4', 'Elettronica', 3, TRUE),
('Macchina Telecomandata RC', 'Veicolo fuoristrada radiocomandato ad alta velocità (fino a 25 km/h) con trazione integrale.', 45.00, 'uploads/Foto/MacchinaTelecomandata.jpg', 'uploads/Video/MacchinaTelecomandata.mp4', 'Giocattoli', 5, TRUE),
('Monitor PC 27" Curved', 'Monitor gaming curvo con risoluzione Quad HD, refresh rate a 144Hz e supporto HDR.', 229.99, 'uploads/Foto/Monitor.jpg', 'uploads/Video/Monitor.mp4', 'Elettronica', 4, TRUE),
('Mouse Wireless Ergonomico', 'Mouse silenzioso wireless a 2.4 GHz con sensore ottico regolabile fino a 2400 DPI.', 15.99, 'uploads/Foto/MouseWireless.jpg', 'uploads/Video/MouseWireless.mp4', 'Elettronica', 4, TRUE),
('Orologio da Polso Automatico', 'Orologio da polso meccanico con quadrante a vista, cinturino in vero cuoio marrone.', 125.00, 'uploads/Foto/Orologio.jpg', 'uploads/Video/Orologio.mp4', 'Moda', 5, TRUE),
('Pallone da Calcio Premium', 'Pallone da calcio cucito a mano, ideale per allenamenti professionali ed erba naturale.', 19.99, 'uploads/Foto/Pallone.jpg', 'uploads/Video/Pallone.mp4', 'Sport', 4, TRUE),
('Powerbank 20000mAh', 'Batteria esterna portatile a ricarica rapida con 3 uscite USB e display LCD percentuale.', 27.99, 'uploads/Foto/Powerbank.webp', 'uploads/Video/Powerbank.mp4', 'Elettronica', 4, TRUE),
('Purificatore d''Aria HEPA', 'Filtro HEPA a 3 strati rimuove il 99.97% di allergeni, fumo e polveri sottili.', 69.99, 'uploads/Foto/PurificatoreAria.jpg', 'uploads/Video/PurificatoreAria.mp4', 'Casa', 4, TRUE),
('Puzzle Paesaggio 1500 Pezzi', 'Puzzle panoramico raffigurante un tramonto mozzafiato sulle montagne rocciose.', 12.99, 'uploads/Foto/Puzzle.jpg', 'uploads/Video/Puzzle.mp4', 'Giocattoli', 4, TRUE),
('Speaker Bluetooth Portatile', 'Cassa audio wireless impermeabile IPX7 con bassi profondi e 12 ore di autonomia.', 49.99, 'uploads/Foto/Speaker.jpg', 'uploads/Video/Speaker.mp4', 'Elettronica', 5, TRUE),
('Tablet 10" Android', 'Tablet multifunzione con processore Octa-Core, 4GB RAM, 64GB di storage espandibile.', 139.00, 'uploads/Foto/Tablet.jpg', 'uploads/Video/Tablet.mp4', 'Elettronica', 4, TRUE),
('Tappetino da Scrivania XXL', 'Sottomano protettivo in ecopelle impermeabile antiscivolo, dimensioni 90x40 cm.', 14.50, 'uploads/Foto/Tappetino.avif', 'uploads/Video/Tappetino.mp4', 'Casa', 4, TRUE),
('Ventilatore a Piantana', 'Ventilatore a tre velocità con oscillazione automatica orizzontale e altezza regolabile.', 32.99, 'uploads/Foto/Ventilatore.webp', 'uploads/Video/Ventilatore.mp4', 'Casa', 3, TRUE),
('AirPods Pro Gen 2', 'Auricolari bluetooth con cancellazione attiva del rumore e audio spaziale personalizzabile.', 249.00, 'uploads/Foto/airpodspro.jpg', 'uploads/Video/AirPodsPro.mp4', 'Elettronica', 5, TRUE),
('Smart TV LED 43" 4K', 'Televisore Ultra HD con sistema operativo Smart TV, HDR10 e controllo vocale.', 299.99, 'uploads/Foto/tv.jpg', 'uploads/Video/Tv.mp4', 'Elettronica', 4, TRUE);

-- Aggiunta di alcune recensioni di esempio
INSERT INTO reviews (user_id, product_id, rating, comment) VALUES
(2, 23, 5, 'Auricolari eccezionali! La cancellazione del rumore è formidabile, le uso ogni giorno.'),
(2, 6, 5, 'Patatine perfette e fritte senza un goccio d''olio. Consiglio vivamente questa friggitrice ad aria!'),
(2, 4, 4, 'Molto comode e audio pulito nei giochi, la batteria dura tantissimo.');
