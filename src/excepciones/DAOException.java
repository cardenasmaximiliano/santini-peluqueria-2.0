package excepciones;

/**
 * Excepción no verificada que representa un error técnico en la capa de acceso a datos
 * (por ejemplo: la base de datos no está disponible o una sentencia SQL falló).
 * <p>
 * Envuelve a la {@link java.sql.SQLException} original para que las capas superiores
 * no dependan de JDBC, pero conserva la causa para poder diagnosticar el problema.
 */
public class DAOException extends RuntimeException {

    /**
     * Crea una nueva excepción de acceso a datos.
     *
     * @param mensaje descripción del error, pensada para mostrarse al usuario
     * @param causa   excepción original que provocó el error
     */
    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    /**
     * Devuelve un mensaje legible que combina la descripción del error con el
     * detalle técnico de la causa (si existe).
     *
     * @return mensaje completo para mostrar en un cuadro de diálogo
     */
    public String getMensajeCompleto() {
        Throwable causa = getCause();
        if (causa == null || causa.getMessage() == null) {
            return getMessage();
        }
        return getMessage() + "\n\nDetalle técnico: " + causa.getMessage();
    }
}
