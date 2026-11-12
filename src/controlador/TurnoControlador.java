package controlador;

import excepciones.ValidacionException;
import modelo.Turno;
import negocio.ExportadorCSV;
import negocio.GestorClientes;
import negocio.GestorServicios;
import negocio.GestorTurnos;
import negocio.Validaciones;
import vista.PanelTurnos;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Controlador de la pantalla de turnos: conecta los eventos de {@link PanelTurnos}
 * con {@link GestorTurnos}. También usa los gestores de clientes y servicios
 * para cargar los combos con los datos reales de la base.
 */
public final class TurnoControlador extends ControladorBase<PanelTurnos> {

    private final GestorTurnos gestorTurnos;
    private final GestorClientes gestorClientes;
    private final GestorServicios gestorServicios;

    /**
     * Crea el controlador, registra los eventos y carga combos y tabla.
     *
     * @param vista           pantalla de turnos
     * @param gestorTurnos    capa de negocio de turnos
     * @param gestorClientes  capa de negocio de clientes (para el combo de clientes)
     * @param gestorServicios capa de negocio de servicios (para el combo de servicios)
     */
    public TurnoControlador(PanelTurnos vista, GestorTurnos gestorTurnos,
                            GestorClientes gestorClientes, GestorServicios gestorServicios) {
        super(vista);
        this.gestorTurnos = gestorTurnos;
        this.gestorClientes = gestorClientes;
        this.gestorServicios = gestorServicios;
        registrarEventos();
        actualizarVista();
    }

    private void registrarEventos() {
        vista.getBotonReservar().addActionListener(e -> reservar());
        vista.getBotonModificar().addActionListener(e -> modificar());
        vista.getBotonCancelar().addActionListener(e -> cancelar());
        vista.getBotonEliminar().addActionListener(e -> eliminar());
        vista.getBotonLimpiar().addActionListener(e -> vista.limpiarFormulario());
        vista.getBotonExportar().addActionListener(e -> exportar());
        vista.getTablaTurnos().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Turno seleccionado = vista.getTurnoSeleccionado();
                if (seleccionado != null) {
                    vista.cargarFormulario(seleccionado);
                }
            }
        });
    }

    /**
     * Convierte los datos del formulario en un {@link Turno}.
     *
     * @return turno con los datos del formulario (sin id)
     * @throws ValidacionException si la fecha o la hora no tienen un formato válido
     */
    private Turno leerFormulario() throws ValidacionException {
        return new Turno(0,
                vista.getCliente(),
                vista.getServicio(),
                Validaciones.aFecha(vista.getFechaTexto(), "fecha"),
                Validaciones.aHora(vista.getHoraTexto(), "hora"),
                vista.getEstado());
    }

    /**
     * Reserva un turno nuevo con los datos del formulario.
     */
    public void reservar() {
        boolean ok = ejecutarSeguro(() -> gestorTurnos.reservar(leerFormulario()));
        if (ok) {
            actualizarTabla();
            vista.limpiarFormulario();
            vista.mostrarInformacion("Turno reservado con éxito.");
        }
    }

    /**
     * Actualiza el turno seleccionado con los datos del formulario.
     */
    public void modificar() {
        boolean ok = ejecutarSeguro(() -> {
            Turno seleccionado = vista.getTurnoSeleccionado();
            if (seleccionado == null) {
                throw new ValidacionException("Seleccioná un turno de la tabla para modificar.");
            }
            Turno modificado = leerFormulario();
            modificado.setId(seleccionado.getId());
            gestorTurnos.modificar(modificado);
        });
        if (ok) {
            actualizarTabla();
            vista.limpiarFormulario();
            vista.mostrarInformacion("Turno modificado con éxito.");
        }
    }

    /**
     * Cancela el turno seleccionado, previa confirmación. El turno queda en la tabla como "Cancelado".
     */
    public void cancelar() {
        Turno seleccionado = vista.getTurnoSeleccionado();
        if (seleccionado != null && !vista.confirmar("¿Cancelar el turno de "
                + seleccionado.getCliente().getNombreCompleto() + "?")) {
            return;
        }
        boolean ok = ejecutarSeguro(() -> gestorTurnos.cancelar(seleccionado));
        if (ok) {
            actualizarTabla();
            vista.limpiarFormulario();
        }
    }

    /**
     * Elimina definitivamente el turno seleccionado, previa confirmación.
     */
    public void eliminar() {
        Turno seleccionado = vista.getTurnoSeleccionado();
        if (seleccionado != null && !vista.confirmar("¿Eliminar definitivamente el turno de "
                + seleccionado.getCliente().getNombreCompleto() + "?\n"
                + "Si solo no va a venir, es mejor usar \"Cancelar Turno\".")) {
            return;
        }
        boolean ok = ejecutarSeguro(() -> gestorTurnos.eliminar(seleccionado));
        if (ok) {
            actualizarTabla();
            vista.limpiarFormulario();
        }
    }

    /**
     * Exporta a CSV los turnos que se ven en la tabla (respetando el filtro de búsqueda).
     */
    public void exportar() {
        List<Turno> visibles = vista.getTurnosVisibles();
        if (visibles.isEmpty()) {
            vista.mostrarAdvertencia("No hay turnos para exportar.");
            return;
        }
        Path destino = vista.elegirArchivoCSV();
        if (destino == null) {
            return;
        }
        try {
            ExportadorCSV.exportarTurnos(visibles, destino);
            vista.mostrarInformacion("Se exportaron " + visibles.size() + " turno(s) a:\n" + destino);
        } catch (IOException e) {
            vista.mostrarError("No se pudo guardar el archivo. Verificá que no esté abierto en Excel.\n\n"
                    + "Detalle técnico: " + e.getMessage());
        }
    }

    /**
     * Recarga los combos de clientes y servicios (pueden haber cambiado en las otras pantallas)
     * y la tabla de turnos.
     */
    @Override
    public void actualizarVista() {
        ejecutarSeguro(() -> {
            vista.cargarClientes(gestorClientes.listar());
            vista.cargarServicios(gestorServicios.listar());
        });
        actualizarTabla();
    }

    private void actualizarTabla() {
        ejecutarSeguro(() -> vista.mostrarTurnos(gestorTurnos.listar()));
    }
}
