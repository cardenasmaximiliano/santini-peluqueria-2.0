package dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fábrica de conexiones JDBC a la base de datos MySQL.
 * <p>
 * Por defecto se conecta a {@code localhost:3306} con usuario {@code root} y
 * <b>sin contraseña</b> (la configuración que trae XAMPP), y crea la base
 * {@code peluqueria} si no existe. No hace falta configurar nada para usarla.
 * <p>
 * Si se necesita otra configuración, cada valor se puede cambiar (en orden de prioridad):
 * <ol>
 *     <li>con las variables de entorno {@code PELU_DB_URL}, {@code PELU_DB_USER} y {@code PELU_DB_PASSWORD};</li>
 *     <li>con un archivo {@code bd.properties} en la carpeta del proyecto, con las claves
 *         {@code url}, {@code usuario} y {@code clave}.</li>
 * </ol>
 */
public final class ConexionBD {

    private static final String ARCHIVO_CONFIGURACION = "bd.properties";

    private static final String URL_POR_DEFECTO =
            "jdbc:mysql://localhost:3306/peluqueria"
                    + "?createDatabaseIfNotExist=true"
                    + "&useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=America/Argentina/Buenos_Aires"
                    + "&characterEncoding=UTF-8";

    private static final Properties ARCHIVO = leerArchivoConfiguracion();

    private static final String URL = valor("PELU_DB_URL", "url", URL_POR_DEFECTO);
    private static final String USUARIO = valor("PELU_DB_USER", "usuario", "root");
    private static final String CLAVE = valor("PELU_DB_PASSWORD", "clave", "");

    private ConexionBD() {
    }

    /**
     * Abre una nueva conexión a la base de datos.
     * Quien la pide es responsable de cerrarla (idealmente con try-with-resources).
     *
     * @return conexión abierta
     * @throws SQLException si el servidor no está disponible o los datos de acceso son incorrectos
     */
    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    /**
     * @return URL JDBC en uso, útil para mostrar en mensajes de error
     */
    public static String getUrl() {
        return URL;
    }

    /**
     * @return usuario de la base de datos en uso
     */
    public static String getUsuario() {
        return USUARIO;
    }

    private static String valor(String variableEntorno, String claveArchivo, String porDefecto) {
        String v = System.getenv(variableEntorno);
        if (v != null && !v.isEmpty()) {
            return v;
        }
        v = ARCHIVO.getProperty(claveArchivo);
        if (v != null && !v.isEmpty()) {
            return v.trim();
        }
        return porDefecto;
    }

    private static Properties leerArchivoConfiguracion() {
        Properties propiedades = new Properties();
        Path ruta = Paths.get(ARCHIVO_CONFIGURACION);
        if (Files.exists(ruta)) {
            try (InputStream in = Files.newInputStream(ruta)) {
                propiedades.load(in);
            } catch (IOException e) {
                System.err.println("No se pudo leer " + ARCHIVO_CONFIGURACION + ": " + e.getMessage());
            }
        }
        return propiedades;
    }
}
