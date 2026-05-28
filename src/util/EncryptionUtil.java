package util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static SecretKeySpec secretKey;
    private static IvParameterSpec ivParameter;

    static {
        // Leggi la chiave e l'IV dalle variabili d'ambiente.
        String keyString = System.getenv("ENCRYPTION_KEY");
        String ivString = System.getenv("ENCRYPTION_IV");

        if (keyString == null || ivString == null) {
            System.err.println("ERRORE CRITICO: Le variabili d'ambiente ENCRYPTION_KEY e ENCRYPTION_IV non sono impostate.");
        } else {
            // La chiave AES deve essere di 16, 24, o 32 byte
            secretKey = new SecretKeySpec(keyString.getBytes(StandardCharsets.UTF_8), "AES");
            // L'IV deve essere di 16 byte per AES.
            ivParameter = new IvParameterSpec(ivString.getBytes(StandardCharsets.UTF_8));
        }
    }

    //Cifra una stringa di testo.

    public static String encrypt(String plainText) throws Exception {
        if (secretKey == null || ivParameter == null) {
            throw new IllegalStateException("Le chiavi di cifratura non sono state inizializzate correttamente.");
        }
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameter);
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(cipherText);
    }

    //Decifra una stringa cifrata.

    public static String decrypt(String cipherText) throws Exception {
        if (secretKey == null || ivParameter == null) {
            throw new IllegalStateException("Le chiavi di cifratura non sono state inizializzate correttamente.");
        }
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameter);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText, StandardCharsets.UTF_8);
    }

    //Metodo main di utility per generare una nuova chiave e IV sicuri
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16]; // 128 bit
        byte[] iv = new byte[16];  // 128 bit
        random.nextBytes(key);
        random.nextBytes(iv);
        
        System.out.println("Salva queste righe come variabili d'ambiente:");
        System.out.println("---------------------------------------------");
        System.out.println("ENCRYPTION_KEY=" + new String(key, StandardCharsets.UTF_8));
        System.out.println("ENCRYPTION_IV=" + new String(iv, StandardCharsets.UTF_8));
        System.out.println("---------------------------------------------");
    }
}