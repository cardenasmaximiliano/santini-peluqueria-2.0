package vista;

import modelo.Cliente;
import modelo.EstadoTurno;
import modelo.Servicio;
import modelo.Turno;
import negocio.Validaciones;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pantalla de gestión de turnos: combina un cliente y un servicio en una fecha y hora.
 * <p>
 * Los combos de cliente y servicio se cargan con los datos reales de la base
 * (los provee el controlador). El diseño está en {@code PanelTurnos.form}.
 */
public class PanelTurnos extends PanelBase {

    private JPanel panelPrincipal;
    private JComboBox<Cliente> comboCliente;
    private JComboBox<Servicio> comboServicio;
    private JTextField fechaTX;
    private JTextField horaTX;
    private JComboBox<EstadoTurno> comboEstado;
    private JTextField filtroTX;

    private JButton botonReservar;
    private JButton botonModificar;
    private JButton botonCancelar;
    private JButton botonEliminar;
    private JButton botonLimpiar;
    private JButton botonExportar;

    private JLabel turno;
    private JLabel cliente;
    private JLabel servicio;
    private JLabel fecha;
    private JLabel hora;
    private JLabel estado;

    private JTable tablaTurnos;
    private JScrollPane tabla_turnos;
    private JPanel panel_botones;

    private DefaultTableModel modeloTabla;
    private List<Turno> turnosMostrados;

    /**
     * Crea la pantalla. Los componentes ya fueron creados por el diseñador de formularios.
     */
    public PanelTurnos() {
        inicializar();
    }

    private void inicializar() {
        turnosMostrados = new ArrayList<>();
        modeloTabla = configurarTabla(tablaTurnos,
                "ID", "Fecha", "Hora", "Fin", "Cliente", "Servicio", "Precio", "Estado");
        tablaTurnos.getColumnModel().getColumn(0).setMaxWidth(50);
        activarBuscador(tablaTurnos, filtroTX);

        comboEstado.removeAllItems();
        for (EstadoTurno e : EstadoTurno.values()) {
            comboEstado.addItem(e);
        }
    }

    @Override
    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    /** @return botón "Reservar Turno" */
    public JButton getBotonReservar() { return botonReservar; }

    /** @return botón "Modificar" */
    public JButton getBotonModificar() { return botonModificar; }

    /** @return botón "Cancelar Turno" */
    public JButton getBotonCancelar() { return botonCancelar; }

    /** @return botón "Eliminar" */
    public JButton getBotonEliminar() { return botonEliminar; }

    /** @return botón "Limpiar" */
    public JButton getBotonLimpiar() { return botonLimpiar; }

    /** @return botón "Exportar CSV" */
    public JButton getBotonExportar() { return botonExportar; }

    /** @return tabla de turnos */
    public JTable getTablaTurnos() { return tablaTurnos; }

    /**
     * Carga el combo de clientes, conservando la selección actual si sigue existiendo.
     *
     * @param clientes clientes disponibles
     */
    public void cargarClientes(List<Cliente> clientes) {
        Object seleccionado = comboCliente.getSelectedItem();
        comboCliente.removeAllItems();
        for (Cliente c : clientes) {
            comboCliente.addItem(c);
        }
        comboCliente.setSelectedItem(clientes.contains(seleccionado) ? seleccionado : null);
    }

    /**
     * Carga el combo de servicios, conservando la selección actual si sigue existiendo.
     *
     * @param servicios servicios disponibles
     */
    public void cargarServicios(List<Servicio> servicios) {
        Object seleccionado = comboServicio.getSelectedItem();
        comboServicio.removeAllItems();
        for (Servicio s : servicios) {
            comboServicio.addItem(s);
        }
        comboServicio.setSelectedItem(servicios.contains(seleccionado) ? seleccionado : null);
    }

    /**
     * Reemplaza el contenido de la tabla por la lista recibida.
     *
     * @param lista turnos a mostrar
     */
    public void mostrarTurnos(List<Turno> lista) {
        turnosMostrados = new ArrayList<>(lista);
        NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"));
        modeloTabla.setRowCount(0);
        for (Turno t : turnosMostrados) {
            modeloTabla.addRow(new Object[]{
                    t.getId(),
                    t.getFecha().format(Validaciones.FORMATO_FECHA_SALIDA),
                    t.getHora().toString(),
                    t.getHoraFin().toString(),
                    t.getCliente().getNombreCompleto(),
                    t.getServicio().getNombre(),
                    moneda.format(t.getServicio().getPrecio()),
                    t.getEstado()
            });
        }
    }

    /**
     * @return el turno de la fila seleccionada, o {@code null} si no hay selección
     */
    public Turno getTurnoSeleccionado() {
        int fila = filaSeleccionadaEnModelo(tablaTurnos);
        return fila < 0 ? null : turnosMostrados.get(fila);
    }

    /**
     * @return los turnos que se ven en la tabla, respetando el filtro y el orden aplicados
     */
    public List<Turno> getTurnosVisibles() {
        List<Turno> visibles = new ArrayList<>();
        for (int filaVista = 0; filaVista < tablaTurnos.getRowCount(); filaVista++) {
            visibles.add(turnosMostrados.get(tablaTurnos.convertRowIndexToModel(filaVista)));
        }
        return visibles;
    }

    /** @return cliente elegido en el combo, o {@code null} */
    public Cliente getCliente() { return (Cliente) comboCliente.getSelectedItem(); }

    /** @return servicio elegido en el combo, o {@code null} */
    public Servicio getServicio() { return (Servicio) comboServicio.getSelectedItem(); }

    /** @return texto del campo fecha */
    public String getFechaTexto() { return fechaTX.getText(); }

    /** @return texto del campo hora */
    public String getHoraTexto() { return horaTX.getText(); }

    /** @return estado elegido en el combo */
    public EstadoTurno getEstado() { return (EstadoTurno) comboEstado.getSelectedItem(); }

    /**
     * Completa el formulario con los datos de un turno.
     *
     * @param t turno a mostrar
     */
    public void cargarFormulario(Turno t) {
        comboCliente.setSelectedItem(t.getCliente());
        comboServicio.setSelectedItem(t.getServicio());
        fechaTX.setText(t.getFecha().format(Validaciones.FORMATO_FECHA_SALIDA));
        horaTX.setText(t.getHora().toString());
        comboEstado.setSelectedItem(t.getEstado());
    }

    /**
     * Vacía el formulario y quita la selección de la tabla.
     */
    public void limpiarFormulario() {
        comboCliente.setSelectedItem(null);
        comboServicio.setSelectedItem(null);
        fechaTX.setText("");
        horaTX.setText("");
        comboEstado.setSelectedItem(EstadoTurno.PENDIENTE);
        tablaTurnos.clearSelection();
    }

    /**
     * Abre un diálogo para elegir dónde guardar el archivo CSV.
     *
     * @return ruta elegida (con extensión .csv), o {@code null} si el usuario canceló
     */
    public Path elegirArchivoCSV() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Exportar turnos");
        selector.setFileFilter(new FileNameExtensionFilter("Archivo CSV (Excel)", "csv"));
        selector.setSelectedFile(new File("turnos_" + LocalDate.now() + ".csv"));
        if (selector.showSaveDialog(panelPrincipal) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".csv")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".csv");
        }
        return archivo.toPath();
    }
}
