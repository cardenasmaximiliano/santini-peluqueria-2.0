package dao;

import excepciones.DAOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Crea las tablas del sistema si todavía no existen.
 * <p>
 * Se ejecuta una vez al iniciar la aplicación, así no hace falta correr
 * ningún script SQL a mano. La base de datos en sí la crea el driver gracias
 * al parámetro {@code createDatabaseIfNotExist=true} de la URL.
 */
public final class InicializadorBD {

    private static final String[] SENTENCIAS = {
            "CREATE TABLE IF NOT EXISTS clientes ("
                    + " id INT AUTO_INCREMENT PRIMARY KEY,"
                    + " nombre VARCHAR(100) NOT NULL,"
                    + " apellido VARCHAR(100) NOT NULL,"
                    + " contacto VARCHAR(100),"
                    + " categoria VARCHAR(20) NOT NULL DEFAULT 'NUEVO',"
                    + " promociones BOOLEAN NOT NULL DEFAULT FALSE"
                    + ")",
            "CREATE TABLE IF NOT EXISTS servicios ("
                    + " id INT AUTO_INCREMENT PRIMARY KEY,"
                    + " nombre VARCHAR(100) NOT NULL,"
                    + " duracion INT NOT NULL,"
                    + " precio DECIMAL(10,2) NOT NULL"
                    + ")",
            "CREATE TABLE IF NOT EXISTS turnos ("
                    + " id INT AUTO_INCREMENT PRIMARY KEY,"
                    + " cliente_id INT NOT NULL,"
                    + " servicio_id INT NOT NULL,"
                    + " fecha DATE NOT NULL,"
                    + " hora TIME NOT NULL,"
                    + " estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',"
                    + " FOREIGN KEY (cliente_id) REFERENCES clientes(id),"
                    + " FOREIGN KEY (servicio_id) REFERENCES servicios(id)"
                    + ")"
    };

    private InicializadorBD() {
    }

    /**
     * Crea las tablas {@code clientes}, {@code servicios} y {@code turnos} si no existen.
     *
     * @throws DAOException si no se puede conectar al servidor o crear las tablas
     */
    public static void crearTablasSiNoExisten() {
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement()) {
            for (String sql : SENTENCIAS) {
                st.executeUpdate(sql);
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo conectar a la base de datos.\n\n"
                    + "Verificá que MySQL esté iniciado (por ejemplo desde el panel de XAMPP).\n"
                    + "URL: " + ConexionBD.getUrl() + "\n"
                    + "Usuario: " + ConexionBD.getUsuario(), e);
        }
    }
}
