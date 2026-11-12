package controlador;

import excepciones.DAOException;
import excepciones.ValidacionException;
import vista.PanelBase;

/**
 * Clase base de los controladores.
 * <p>
 * Centraliza el manejo de excepciones de todas las acciones del usuario:
 * <ul>
 *     <li>{@link ValidacionException}: dato mal ingresado, se muestra como advertencia.</li>
 *     <li>{@link DAOException}: problema con la base de datos, se muestra como error.</li>
 *     <li>Cualquier otra excepción inesperada: se informa sin cerrar el programa.</li>
 * </ul>
 *
 * @param <V> tipo de pantalla que controla
 */
public abstract class ControladorBase<V extends PanelBase> {

    /** Pantalla controlada. */
    protected final V vista;

    /**
     * @param vista pantalla controlada
     */
    protected ControladorBase(V vista) {
        this.vista = vista;
    }

    /**
     * Acción del usuario que puede fallar por una regla de negocio.
     */
    @FunctionalInterface
    protected interface Accion {
        /**
         * Ejecuta la acción.
         *
         * @throws ValidacionException si algún dato es inválido
         */
        void ejecutar() throws ValidacionException;
    }

    /**
     * Ejecuta una acción mostrando un mensaje adecuado si falla.
     *
     * @param accion acción a ejecutar
     * @return {@code true} si la acción terminó sin errores
     */
    protected boolean ejecutarSeguro(Accion accion) {
        try {
            accion.ejecutar();
            return true;
        } catch (ValidacionException e) {
            vista.mostrarAdvertencia(e.getMessage());
        } catch (DAOException e) {
            vista.mostrarError(e.getMensajeCompleto());
        } catch (RuntimeException e) {
            e.printStackTrace();
            vista.mostrarError("Ocurrió un error inesperado: " + e);
        }
        return false;
    }

    /**
     * Vuelve a leer los datos de la base y los muestra en la pantalla.
     */
    public abstract void actualizarVista();
}
