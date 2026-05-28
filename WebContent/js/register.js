
document.addEventListener('DOMContentLoaded', function() {
    
    const form = document.getElementById('registerForm');
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const submitBtn = document.getElementById('submitBtn');

    const nameError = document.getElementById('nameError');
    const emailError = document.getElementById('emailError');
    const passwordError = document.getElementById('passwordError');

    const validity = {
        name: false,
        email: false,
        emailAvailable: false,
        password: false
    };

    function setFieldError(inputElement, errorElement, message) {
        if (message) {
            errorElement.textContent = message;
            errorElement.classList.add('visible');
            inputElement.classList.add('invalid');
        } else {
            errorElement.classList.remove('visible');
            inputElement.classList.remove('invalid');
        }
    }

    function checkFormValidity() {
        const isFormValid = validity.name && validity.email && validity.emailAvailable && validity.password;
        submitBtn.disabled = !isFormValid;
    }

    nameInput.addEventListener('input', () => {
        if (nameInput.value.trim() === "") {
            setFieldError(nameInput, nameError, "Il nome è obbligatorio.");
            validity.name = false;
        } else {
            setFieldError(nameInput, nameError, null);
            validity.name = true;
        }
        checkFormValidity();
    });

    emailInput.addEventListener('input', () => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(emailInput.value)) {
            setFieldError(emailInput, emailError, "Inserisci un formato email valido.");
            validity.email = false;
            validity.emailAvailable = false;
        } else {
            setFieldError(emailInput, emailError, null);
            validity.email = true;
        }
        checkFormValidity();
    });

    emailInput.addEventListener('blur', () => {
        if (validity.email) {
            const email = emailInput.value.trim();
            setFieldError(emailInput, emailError, "Verifica in corso...");
            
            fetch(`CheckEmailServlet?email=${encodeURIComponent(email)}`)
                .then(response => {
                    if (!response.ok) throw new Error("Errore di rete");
                    return response.json();
                })
                .then(data => {
                    if (data.exists) {
                        setFieldError(emailInput, emailError, "Questa email è già registrata.");
                        validity.emailAvailable = false;
                    } else {
                        setFieldError(emailInput, emailError, null);
                        validity.emailAvailable = true;
                    }
                    checkFormValidity();
                })
                .catch(error => {
                    console.error("Errore nella verifica email:", error);
                    setFieldError(emailInput, emailError, "Impossibile verificare l'email ora.");
                    validity.emailAvailable = false;
                    checkFormValidity();
                });
        }
    });

    passwordInput.addEventListener('input', () => {
        const password = passwordInput.value;
        const instructions = passwordInput.parentElement.querySelector('.password-instructions');
        let message = "";

        if (password.length < 8) {
            message = "La password deve essere di almeno 8 caratteri.";
        } else if (!/[A-Z]/.test(password)) {
            message = "Deve contenere almeno una lettera maiuscola.";
        } else if (!/[a-z]/.test(password)) {
            message = "Deve contenere almeno una lettera minuscola.";
        } else if (!/\d/.test(password)) {
            message = "Deve contenere almeno un numero.";
        }

        setFieldError(passwordInput, passwordError, message);
     
        if (instructions) {
            instructions.classList.toggle('invalid-password', !!message);
        }
        
        validity.password = !message;
        checkFormValidity();
    });
    
    form.addEventListener('submit', (event) => {
        if (submitBtn.disabled) {
            event.preventDefault();
        }
    });
});