package vista;

import modelo.Servicio;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pantalla de ABM (alta, baja, modificación y consulta) de servicios.
 * <p>
 * Los componentes se diseñan en {@code PanelServicios.form} (diseñador de IntelliJ).
 * La conversión de los textos a números la hace el controlador.
 */
public class PanelServicios extends PanelBase {

    private JPanel panelPrincipal;
    private JTextField nombreTX;
    private JTextField duracionTX;
    private JTextField precioTX;
    private JTextField filtroTX;

    private JButton botonGuardarServicio;
    private JButton botonModificar;
    private JButton botonEliminar;
    private JButton botonLimpiar;

    private JLabel servicio;
    private JLabel nombre;
    private JLabel duracion;
    private JLabel precio;

    private JTable tablaServicios;
    private JScrollPane tabla_servicios;
    private JPanel panel_botones;

    private DefaultTableModel modeloTabla;
    private List<Servicio> serviciosMostrados;

    /**
     * Crea la pantalla. Los componentes ya fueron creados por el diseñador de formularios.
     */
    public PanelServicios() {
        inicializar();
    }

    private void inicializar() {
        serviciosMostrados = new ArrayList<>();
        modeloTabla = configurarTabla(tablaServicios, "ID", "Nombre", "Duración (min)", "Precio");
        tablaServicios.getColumnModel().getColumn(0).setMaxWidth(50);
        activarBuscador(tablaServicios, filtroTX);
    }

    @Override
    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    /** @return botón "Guardar" */
    public JButton getBotonGuardar() { return botonGuardarServicio; }

    /** @return botón "Modificar" */
    public JButton getBotonModificar() { return botonModificar; }

    /** @return botón "Eliminar" */
    public JButton getBotonEliminar() { return botonEliminar; }

    /** @return botón "Limpiar" */
    public JButton getBotonLimpiar() { return botonLimpiar; }

    /** @return tabla de servicios */
    public JTable getTablaServicios() { return tablaServicios; }

    /** @return texto del campo nombre */
    public String getNombre() { return nombreTX.getText(); }

    /** @return texto del campo duración */
    public String getDuracion() { return duracionTX.getText(); }

    /** @return texto del campo precio */
    public String getPrecio() { return precioTX.getText(); }

    /**
     * Reemplaza el contenido de la tabla por la lista recibida.
     *
     * @param lista servicios a mostrar
     */
    public void mostrarServicios(List<Servicio> lista) {
        serviciosMostrados = new ArrayList<>(lista);
        NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"));
        modeloTabla.setRowCount(0);
        for (Servicio s : serviciosMostrados) {
            modeloTabla.addRow(new Object[]{
                    s.getId(), s.getNombre(), s.getDuracionMinutos(), moneda.format(s.getPrecio())
            });
        }
    }

    /**
     * @return el servicio de la fila seleccionada, o {@code null} si no hay selección
     */
    public Servicio getServicioSeleccionado() {
        int fila = filaSeleccionadaEnModelo(tablaServicios);
        return fila < 0 ? null : serviciosMostrados.get(fila);
    }

    /**
     * Completa el formulario con los datos de un servicio.
     *
     * @param s servicio a mostrar
     */
    public void cargarFormulario(Servicio s) {
        nombreTX.setText(s.getNombre());
        duracionTX.setText(String.valueOf(s.getDuracionMinutos()));
        precioTX.setText(s.getPrecio().toPlainString().replace('.', ','));
    }

    /**
     * Vacía el formulario y quita la selección de la tabla.
     */
    public void limpiarFormulario() {
        nombreTX.setText("");
        duracionTX.setText("");
        precioTX.setText("");
        tablaServicios.clearSelection();
        nombreTX.requestFocusInWindow();
    }
}
