package vista;

import modelo.CategoriaCliente;
import modelo.Cliente;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla de ABM (alta, baja, modificación y consulta) de clientes.
 * <p>
 * Los componentes se diseñan en {@code PanelCliente.form} (diseñador de IntelliJ).
 * Esta clase solo sabe mostrar y leer datos: no valida reglas ni accede a la base;
 * eso lo hacen el controlador y la capa de negocio.
 */
public class PanelCliente extends PanelBase {

    private JPanel panelPrincipal;
    private JPanel panel_botones;
    private JScrollPane tabla_clientes;

    private JLabel clientes;
    private JLabel nombre;
    private JLabel apellido;
    private JLabel contacto;
    private JLabel Categoria;

    private JTextField nombreTX;
    private JTextField apellidoTX;
    private JTextField contactoTX;
    private JComboBox<CategoriaCliente> comboCategoria;
    private JCheckBox promociones;
    private JTextField filtroTX;

    private JButton botonGuardarCliente;
    private JButton botonModificar;
    private JButton botonEliminar;
    private JButton botonLimpiar;

    private JTable tablaClientes;

    private DefaultTableModel modeloTabla;
    private List<Cliente> clientesMostrados;

    /**
     * Crea la pantalla. Los componentes ya fueron creados por el diseñador de formularios.
     */
    public PanelCliente() {
        inicializar();
    }

    private void inicializar() {
        clientesMostrados = new ArrayList<>();
        modeloTabla = configurarTabla(tablaClientes,
                "ID", "Nombre", "Apellido", "Contacto", "Categoría", "Promociones");
        tablaClientes.getColumnModel().getColumn(0).setMaxWidth(50);
        activarBuscador(tablaClientes, filtroTX);

        comboCategoria.removeAllItems();
        for (CategoriaCliente categoria : CategoriaCliente.values()) {
            comboCategoria.addItem(categoria);
        }
    }

    @Override
    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    /** @return botón "Guardar" */
    public JButton getBotonGuardar() { return botonGuardarCliente; }

    /** @return botón "Modificar" */
    public JButton getBotonModificar() { return botonModificar; }

    /** @return botón "Eliminar" */
    public JButton getBotonEliminar() { return botonEliminar; }

    /** @return botón "Limpiar" */
    public JButton getBotonLimpiar() { return botonLimpiar; }

    /** @return tabla de clientes */
    public JTable getTablaClientes() { return tablaClientes; }

    /**
     * Reemplaza el contenido de la tabla por la lista recibida.
     *
     * @param lista clientes a mostrar
     */
    public void mostrarClientes(List<Cliente> lista) {
        clientesMostrados = new ArrayList<>(lista);
        modeloTabla.setRowCount(0);
        for (Cliente c : clientesMostrados) {
            modeloTabla.addRow(new Object[]{
                    c.getId(),
                    c.getNombre(),
                    c.getApellido(),
                    c.getContacto() == null ? "" : c.getContacto(),
                    c.getCategoria(),
                    c.isPromociones() ? "Sí" : "No"
            });
        }
    }

    /**
     * @return el cliente de la fila seleccionada, o {@code null} si no hay selección
     */
    public Cliente getClienteSeleccionado() {
        int fila = filaSeleccionadaEnModelo(tablaClientes);
        return fila < 0 ? null : clientesMostrados.get(fila);
    }

    /**
     * Arma un cliente con lo escrito en el formulario (sin id).
     *
     * @return cliente con los datos del formulario
     */
    public Cliente leerFormulario() {
        return new Cliente(0,
                nombreTX.getText(),
                apellidoTX.getText(),
                contactoTX.getText(),
                (CategoriaCliente) comboCategoria.getSelectedItem(),
                promociones.isSelected());
    }

    /**
     * Completa el formulario con los datos de un cliente.
     *
     * @param c cliente a mostrar
     */
    public void cargarFormulario(Cliente c) {
        nombreTX.setText(c.getNombre());
        apellidoTX.setText(c.getApellido());
        contactoTX.setText(c.getContacto() == null ? "" : c.getContacto());
        comboCategoria.setSelectedItem(c.getCategoria());
        promociones.setSelected(c.isPromociones());
    }

    /**
     * Vacía el formulario y quita la selección de la tabla.
     */
    public void limpiarFormulario() {
        nombreTX.setText("");
        apellidoTX.setText("");
        contactoTX.setText("");
        comboCategoria.setSelectedItem(CategoriaCliente.NUEVO);
        promociones.setSelected(false);
        tablaClientes.clearSelection();
        nombreTX.requestFocusInWindow();
    }
}
