package util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {

    private static String url = "jdbc:mysql://localhost:3306/tikshop?useSSL=false&serverTimezone=UTC";
    private static String user = "root";
    private static String password = ""; // Default sicuro (senza password)

    static {
        try {
            // Carica il driver MySQL una sola volta all'avvio
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Prova a caricare il file db.properties dal classpath o dalla cartella radice
            Properties props = new Properties();
            try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input != null) {
                    props.load(input);
                    url = props.getProperty("db.url", url);
                    user = props.getProperty("db.username", user);
                    password = props.getProperty("db.password", password);
                } else {
                    // Cerca il file properties nella radice locale (utile per lo sviluppo)
                    try (InputStream localInput = new java.io.FileInputStream("db.properties")) {
                        props.load(localInput);
                        url = props.getProperty("db.url", url);
                        user = props.getProperty("db.username", user);
                        password = props.getProperty("db.password", password);
                    } catch (Exception ignored) {
                        // Utilizza i fallback predefiniti
                        System.out.println("DBUtil Info: db.properties non trovato nel classpath o nella radice. Uso dei parametri predefiniti.");
                    }
                }
            } catch (Exception e) {
                System.err.println("DBUtil Errore: Errore durante il caricamento di db.properties: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Driver MySQL non trovato! Assicurati che il JAR del connettore sia in WEB-INF/lib.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}