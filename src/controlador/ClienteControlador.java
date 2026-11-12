package controlador;

import excepciones.ValidacionException;
import modelo.Cliente;
import negocio.GestorClientes;
import vista.PanelCliente;

/**
 * Controlador de la pantalla de clientes: conecta los eventos de {@link PanelCliente}
 * con las operaciones de {@link GestorClientes}.
 */
public final class ClienteControlador extends ControladorBase<PanelCliente> {

    private final GestorClientes gestor;

    /**
     * Crea el controlador, registra los eventos y carga la tabla.
     *
     * @param vista  pantalla de clientes
     * @param gestor capa de negocio de clientes
     */
    public ClienteControlador(PanelCliente vista, GestorClientes gestor) {
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
        vista.getTablaClientes().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Cliente seleccionado = vista.getClienteSeleccionado();
                if (seleccionado != null) {
                    vista.cargarFormulario(seleccionado);
                }
            }
        });
    }

    /**
     * Registra un cliente nuevo con los datos del formulario.
     */
    public void guardar() {
        boolean ok = ejecutarSeguro(() -> gestor.registrar(vista.leerFormulario()));
        if (ok) {
            actualizarVista();
            vista.limpiarFormulario();
            vista.mostrarInformacion("Cliente guardado con éxito.");
        }
    }

    /**
     * Actualiza el cliente seleccionado con los datos del formulario.
     */
    public void modificar() {
        boolean ok = ejecutarSeguro(() -> {
            Cliente seleccionado = vista.getClienteSeleccionado();
            if (seleccionado == null) {
                throw new ValidacionException("Seleccioná un cliente de la tabla para modificar.");
            }
            Cliente modificado = vista.leerFormulario();
            modificado.setId(seleccionado.getId());
            gestor.modificar(modificado);
        });
        if (ok) {
            actualizarVista();
            vista.limpiarFormulario();
            vista.mostrarInformacion("Cliente modificado con éxito.");
        }
    }

    /**
     * Elimina el cliente seleccionado, previa confirmación.
     */
    public void eliminar() {
        Cliente seleccionado = vista.getClienteSeleccionado();
        if (seleccionado != null
                && !vista.confirmar("¿Eliminar al cliente " + seleccionado.getNombreCompleto() + "?")) {
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
        ejecutarSeguro(() -> vista.mostrarClientes(gestor.listar()));
    }
}
