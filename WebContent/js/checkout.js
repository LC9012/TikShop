document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('checkout-form');
    if (!form) return;

    const submitBtn = document.getElementById('submit-btn');

    // Mappa di tutti i campi da validare
    const fields = {
        shippingAddress: { input: document.getElementById('shippingAddress'), error: document.getElementById('addressError'), valid: false },
        cardHolderName: { input: document.getElementById('cardHolderName'), error: document.getElementById('cardNameError'), valid: false },
        cardNumber: { input: document.getElementById('cardNumber'), error: document.getElementById('cardNumberError'), valid: false },
        cardExpiry: { input: document.getElementById('cardExpiry'), error: document.getElementById('cardExpiryError'), valid: false },
        cardCvv: { input: document.getElementById('cardCvv'), error: document.getElementById('cardCvvError'), valid: false }
    };


    // Utility per mostrare nascondere errori
    function setFieldError(field, isValid, message) {
        field.valid = isValid;
        field.error.textContent = isValid ? '' : message;
        field.error.style.display = isValid ? 'block' : 'none';
        field.error.style.visibility = isValid ? 'hidden' : 'visible';
        field.input.classList.toggle('invalid', !isValid);
    }

    // Abilita/disabilita il pulsante di submit
    function checkFormValidity() {
        submitBtn.disabled = !Object.values(fields).every(field => field.valid);
    }

    //Indirizzo e Nome
    fields.shippingAddress.input.addEventListener('input', () => setFieldError(fields.shippingAddress, fields.shippingAddress.input.value.trim() !== '', 'L\'indirizzo è obbligatorio.'));
    fields.cardHolderName.input.addEventListener('input', () => setFieldError(fields.cardHolderName, /^[a-zA-Z\s']+$/.test(fields.cardHolderName.input.value.trim()), 'Inserisci un nome valido.'));

    //Numero Carta 
    fields.cardNumber.input.addEventListener('input', (e) => {
        let value = e.target.value.replace(/\D/g, '').substring(0, 16);
        e.target.value = value.replace(/(.{4})/g, '$1 ').trim();
        setFieldError(fields.cardNumber, value.length === 16, 'Il numero della carta deve essere di 16 cifre.');
    });
 
    fields.cardExpiry.input.addEventListener('input', (e) => {
        let value = e.target.value.replace(/\D/g, '').substring(0, 4);
        if (value.length > 2) {
            value = value.slice(0, 2) + ' / ' + value.slice(2);
        }
        e.target.value = value;
        
        const [month, year] = value.split(' / ');
        const isValid = /^(0[1-9]|1[0-2])\s\/\s\d{2}$/.test(value) && year && parseInt(year, 10) >= new Date().getFullYear() % 100;
        setFieldError(fields.cardExpiry, isValid, 'Data non valida (MM / YY).');
    });

    //CVV 
    fields.cardCvv.input.addEventListener('input', (e) => {
        let value = e.target.value.replace(/\D/g, '').substring(0, 4);
        e.target.value = value;
        setFieldError(fields.cardCvv, value.length >= 3, 'Il CVV deve avere 3 o 4 cifre.');
    });


    Object.values(fields).forEach(field => {
        field.input.addEventListener('input', checkFormValidity);
    });
    checkFormValidity(); 
});