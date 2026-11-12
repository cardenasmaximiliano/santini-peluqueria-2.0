package excepciones;

/**
 * Excepción verificada que indica que un dato ingresado por el usuario no cumple
 * una regla del negocio (campo obligatorio vacío, número inválido, turno superpuesto, etc.).
 * <p>
 * Al ser verificada, el compilador obliga a los controladores a tratarla y
 * mostrarle al usuario un mensaje claro en lugar de dejar caer el programa.
 */
public class ValidacionException extends Exception {

    /**
     * Crea una nueva excepción de validación.
     *
     * @param mensaje explicación del problema, pensada para mostrarse al usuario
     */
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
