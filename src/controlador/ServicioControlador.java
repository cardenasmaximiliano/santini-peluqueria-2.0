package controlador;

import excepciones.ValidacionException;
import modelo.Servicio;
import negocio.GestorServicios;
import negocio.Validaciones;
import vista.PanelServicios;

/**
 * Controlador de la pantalla de servicios: conecta los eventos de {@link PanelServicios}
 * con las operaciones de {@link GestorServicios}.
 */
public final class ServicioControlador extends ControladorBase<PanelServicios> {

    private final GestorServicios gestor;

    /**
     * Crea el controlador, registra los eventos y carga la tabla.
     *
     * @param vista  pantalla de servicios
     * @param gestor capa de negocio de servicios
     */
    public ServicioControlador(PanelServicios vista, GestorServicios gestor) {
        super(vista);
        this.gestor = gestor;
        registrarEventos();
        actualizarVista();
    }

    private void registrarEventos() {
        vista.getBotonGuardar().addActionListener(e -> guardar());
        vista.getBotonModificar().addActionListener(e -> modificar());
        vista.getBotonEliminar().addActionListener(e -> eliminar());
        vista.getBotonLimpiar().addActionListener(e -> vista.limpiarFormulario());
        vista.getTablaServicios().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Servicio seleccionado = vista.getServicioSeleccionado();
                if (seleccionado != null) {
                    vista.cargarFormulario(seleccionado);
                }
            }
        });
    }

    /**
     * Convierte los textos del formulario en un {@link Servicio}.
     *
     * @return servicio con los datos del formulario (sin id)
     * @throws ValidacionException si la duración o el precio no son números válidos
     */
    private Servicio leerFormulario() throws ValidacionException {
        int duracion = Validaciones.aEntero(vista.getDuracion(), "duración");
        return new Servicio(0, vista.getNombre(), duracion,
                Validaciones.aImporte(vista.getPrecio(), "precio"));
    }

    /**
     * Registra un servicio nuevo con los datos del formulario.
     */
    public void guardar() {
        boolean ok = ejecutarSeguro(() -> gestor.registrar(leerFormulario()));
        if (ok) {
            actualizarVista();
            vista.limpiarFormulario();
            vista.mostrarInformacion("Servicio guardado con éxito.");
        }
    }

    /**
     * Actualiza el servicio seleccionado con los datos del formulario.
     */
    public void modificar() {
        boolean ok = ejecutarSeguro(() -> {
            Servicio seleccionado = vista.getServicioSeleccionado();
            if (seleccionado == null) {
                throw new ValidacionException("Seleccioná un servicio de la tabla para modificar.");
            }
            Servicio modificado = leerFormulario();
            modificado.setId(seleccionado.getId());
            gestor.modificar(modificado);
        });
        if (ok) {
            actualizarVista();
            vista.limpiarFormulario();
            vista.mostrarInformacion("Servicio modificado con éxito.");
        }
    }

    /**
     * Elimina el servicio seleccionado, previa confirmación.
     */
    public void eliminar() {
        Servicio seleccionado = vista.getServicioSeleccionado();
        if (seleccionado != null
                && !vista.confirmar("¿Eliminar el servicio \"" + seleccionado.getNombre() + "\"?")) {
            return;
        }
        boolean ok = ejecutarSeguro(() -> gestor.eliminar(seleccionado));
        if (ok) {
            actualizarVista();
            vista.limpiarFormulario();
        }
    }

    @Override
    public void actualizarVista() {
        ejecutarSeguro(() -> vista.mostrarServicios(gestor.listar()));
    }
}
