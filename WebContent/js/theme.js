(function() {
    // Elenco dei fogli stile che devono essere cambiati
    const themeConfig = [
        {
            dark: "css/style.css",
            light: "css/style_white.css"
        },
        {
            dark: "css/style_traditional.css",
            light: "css/style_traditional_white.css"
        }
    ];

    // Funzione per ottenere il tema corrente
    function getTheme() {
        return localStorage.getItem("site-theme") === "light" ? "light" : "dark";
    }

    // Funzione per impostare il tema e cambiare i CSS
    function setTheme(theme) {
        localStorage.setItem("site-theme", theme);
        document.querySelectorAll('link[rel="stylesheet"]').forEach(link => {
            themeConfig.forEach(cfg => {
                if (link.href.includes(cfg.dark)) {
                    link.href = link.href.replace(cfg.dark, cfg[theme]);
                } else if (link.href.includes(cfg.light)) {
                    link.href = link.href.replace(cfg.light, cfg[theme]);
                }
            });
        });
        // Aggiorna lo switch se presente
        const switchEl = document.getElementById("themeSwitch");
        if (switchEl) {
            switchEl.checked = (theme === "light");
            const slider = switchEl.nextElementSibling;
            if (slider) {
                slider.classList.remove("force-repaint");
                void slider.offsetWidth;
                slider.classList.add("force-repaint");
                setTimeout(() => slider.classList.remove("force-repaint"), 400);
            }
        }
    }

    // All'avvio applica il tema base
    document.addEventListener("DOMContentLoaded", function() {
        setTheme(getTheme());
        const switchEl = document.getElementById("themeSwitch");
        if(switchEl){
            switchEl.checked = (getTheme() === "light");
            switchEl.addEventListener("change", function() {
                setTheme(this.checked ? "light" : "dark");
            });
        }
    });
})();