#  TikShop - Java Dynamic Web E-Commerce

Benvenuto in **TikShop**, un'innovativa piattaforma di e-commerce accattivante e moderna che unisce la tradizionale esperienza di shopping online con un'interfaccia dinamica e interattiva ispirata a **TikTok**, arricchita da video di presentazione per ciascun prodotto.

Il progetto è stato sviluppato come applicazione web dinamica Java Enterprise (Java EE) basata sull'architettura **Model-View-Controller (MVC)** standard, utilizzando esclusivamente servlet, pagine JSP e JDBC per l'interazione con il database.


---

##  Funzionalità Principali

*   **Interfaccia TikTok-Style (Shopping Innovativo):** Sfoglia i prodotti in verticale visualizzando video dimostrativi a tutto schermo, con la possibilità di aggiungere articoli al carrello e leggere recensioni in tempo reale con gesti rapidi.
*   **Shopping Tradizionale:** Un catalogo grigliato classico e pulito, con filtri avanzati per reparto e ricerca testuale dinamica dei prodotti.
*   **Autenticazione Sicura:** Registrazione ed accesso protetti con cifratura delle password tramite l'algoritmo di hashing **BCrypt** (impedendo il salvataggio di password in chiaro).
*   **Sistema di Ordini & Sicurezza Carte:** Gestione transazionale degli acquisti (con meccanismo di rollback ACID in caso di errore) e crittografia AES (128-bit in modalità CBC con IV) per memorizzare in modo sicuro i dettagli della carta di credito nel database.
*   **Carrello Persistente Integrato:** Gestione intelligente del carrello per utenti ospiti (salvato in sessione) che si fonde automaticamente (merge) nel database al momento del login o della registrazione.
*   **Recensioni e Valutazioni:** Gli utenti possono lasciare una valutazione da 1 a 5 stelle con commenti scritti per i prodotti acquistati, e filtrare i feedback per punteggio.
*   **Pannello di Amministrazione Completo:**
    *   Gestione dei prodotti (aggiunta, attivazione/disattivazione logica degli articoli).
    *   Monitoraggio degli ordini totali con ordinamenti e filtri per stato (Pending, Spedito, ecc.).
    *   Aggiornamento istantaneo del prezzo del catalogo.
    *   **Generazione Fattura PDF:** Esportazione automatica della fattura d'acquisto in formato PDF professionale mediante la libreria **OpenPDF**.

---

## Stack Tecnologico

*   **Backend Core:** Java SE, Java EE Servlet 3.1 & JSP (JavaServer Pages)
*   **Database & Persistenza:** MySQL 8.x, JDBC (Java Database Connectivity) con pool di connessione nativo
*   **Sicurezza:** BCrypt (Hashing password), AES-128 CBC (Crittografia dati di pagamento)
*   **Esportazione:** OpenPDF 1.3.x (Generazione dinamica PDF)
*   **Frontend:** HTML5, CSS3 nativo (layout responsive e glassmorphism), JavaScript (Ajax, micro-animazioni, swipe gesture)
*   **Ambiente di Esecuzione:** Apache Tomcat 9.x (o compatibili)

---

## Architettura del Progetto

Il progetto segue rigorosamente il pattern architetturale **MVC**:

```
TikShopJava/
├── src/
│   ├── control/         # [CONTROLLER] Servlet per la gestione delle richieste e filtri di sicurezza
│   ├── model/           # [MODEL] Oggetti Bean (User, Product, Order) e classi DAO per l'accesso al DB
│   └── util/            # Utility per la crittografia, hashing e connessione sicura al Database
├── WebContent/          # [VIEW] Pagine JSP, frammenti, fogli di stile CSS, JS e risorse statiche
│   ├── WEB-INF/         # web.xml, librerie JAR di terze parti e classi compilate
│   ├── admin/           # Pagine e dashboard riservate all'amministratore
│   ├── css/             # Fogli di stile CSS modulari
│   ├── fragments/       # Frammenti JSP riutilizzabili (header, navbar, footer)
│   ├── js/              # Script JavaScript per interazioni e chiamate asincrone (AJAX)
│   └── uploads/         # Risorse statiche per immagini (Foto/) e presentazioni (Video/) dei prodotti
├── schema.sql           # Script di inizializzazione e seed del database MySQL
└── db.properties.example # Template per la configurazione delle credenziali locali del DB
```

---

## Guida all'Installazione e Avvio

### 1. Prerequisiti
*   **Java Development Kit (JDK):** Versione 8 o superiore (consigliata JDK 11 o 17).
*   **IDE consigliato:** Eclipse IDE for Enterprise Java and Web Developers (oppure IntelliJ IDEA Ultimate / VS Code opportunamente configurati).
*   **Server Web:** Apache Tomcat 9.x.
*   **Database:** MySQL Server 8.0+.

### 2. Configurazione del Database
1.  Avvia la tua istanza locale di MySQL.
2.  Importa lo script SQL fornito per creare il database `tikshop`, definire le tabelle e inserire i prodotti di test:
    ```bash
    mysql -u root -p < schema.sql
    ```
    *(Nota: In alternativa, puoi copiare e incollare il contenuto di `schema.sql` all'interno di un client grafico come MySQL Workbench, DBeaver o phpMyAdmin).*

### 3. Configurazione dell'Applicazione
1.  Nella cartella radice del progetto, copia il file `db.properties.example` e rinominalo in `db.properties`:
    ```bash
    cp db.properties.example db.properties
    ```
2.  Apri `db.properties` ed inserisci il tuo URL di connessione e le credenziali locali (utente e password di MySQL):
    ```properties
    db.url=jdbc:mysql://localhost:3306/tikshop?useSSL=false&serverTimezone=UTC
    db.username=tuo_utente_mysql
    db.password=la_tua_password_mysql
    ```
3.  *Il file `db.properties` è configurato nel `.gitignore` in modo da non essere accidentalmente caricato su GitHub.*

### 4. Configurazione della Sicurezza (Crittografia Carte)
La cifratura dei numeri delle carte di credito richiede che siano configurate sul server (o sul sistema operativo locale) due variabili d'ambiente per evitare di esporre le chiavi nel codice:
*   `ENCRYPTION_KEY`: Una chiave testuale segreta per l'algoritmo AES (es. una stringa alfanumerica di 16 caratteri / 128 bit).
*   `ENCRYPTION_IV`: Un vettore di inizializzazione a 16 caratteri.

*(Se queste variabili non vengono trovate all'avvio, l'applicazione lo segnalerà nel log del server Tomcat).*

### 5. Importazione ed Esecuzione in Eclipse
1.  Apri Eclipse.
2.  Scegli `File` -> `Import...` -> `General` -> `Existing Projects into Workspace`.
3.  Seleziona la cartella principale del progetto `TikShopJava`.
4.  Fai click destro sul progetto in *Project Explorer*, seleziona `Run As` -> `Run on Server`.
5.  Seleziona il tuo server Apache Tomcat 9.x e fai click su **Finish**.
6.  L'applicazione sarà accessibile all'indirizzo: `http://localhost:8080/TikShop/` (o simile, a seconda del contesto di Tomcat).

---

## Credenziali di Test preconfigurate

Lo script `schema.sql` inserisce automaticamente due utenti per testare l'applicazione:

*   **Profilo Cliente (Customer):**
    *   **Email:** `mario.rossi@gmail.com`
    *   **Password:** `password123`
*   **Profilo Amministratore (Admin):**
    *   **Email:** `admin@tikshop.com`
    *   **Password:** `password123`

---

## Membri del Gruppo

Questo progetto è stato realizzato in collaborazione da:
* **Luca Di Gennaro** - [ldigennaro8](https://github.com/ldigennaro8) (Matricola: `0512119432`)
---

##  Licenza ed Utilizzo
Questo progetto è stato sviluppato a scopi didattici accademici. È libero da utilizzare, modificare ed estendere per progetti personali o universitari.
