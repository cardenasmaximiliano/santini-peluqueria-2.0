package vista;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.regex.Pattern;

/**
 * Clase base de todas las pantallas del sistema.
 * <p>
 * Reúne el comportamiento común (mostrar mensajes, pedir confirmación y preparar
 * tablas de solo lectura) para que cada panel concreto solo se ocupe de sus campos.
 */
public abstract class PanelBase {

    /**
     * Constructor para las subclases.
     */
    protected PanelBase() {
    }

    /**
     * @return panel raíz de la pantalla, el que se agrega a la ventana principal
     */
    public abstract JPanel getPanelPrincipal();

    /**
     * Muestra un mensaje informativo.
     *
     * @param mensaje texto a mostrar
     */
    public void mostrarInformacion(String mensaje) {
        JOptionPane.showMessageDialog(getPanelPrincipal(), mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra una advertencia (por ejemplo, un dato mal ingresado).
     *
     * @param mensaje texto a mostrar
     */
    public void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(getPanelPrincipal(), mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Muestra un error grave (por ejemplo, un problema con la base de datos).
     *
     * @param mensaje texto a mostrar
     */
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(getPanelPrincipal(), mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Pide al usuario que confirme una acción.
     *
     * @param mensaje pregunta a mostrar
     * @return {@code true} si el usuario eligió "Sí"
     */
    public boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(getPanelPrincipal(), mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Configura una tabla de solo lectura, con selección de una fila a la vez.
     *
     * @param tabla    tabla a configurar
     * @param columnas títulos de las columnas
     * @return el modelo creado y asignado a la tabla
     */
    protected static DefaultTableModel configurarTabla(JTable tabla, String... columnas) {
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                Object valor = getRowCount() > 0 ? getValueAt(0, columna) : null;
                return valor != null ? valor.getClass() : Object.class;
            }
        };
        tabla.setModel(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setFillsViewportHeight(true);
        return modelo;
    }

    /**
     * Activa el ordenamiento por columna (clic en el encabezado) y un buscador en vivo:
     * a medida que se escribe en {@code campoBusqueda}, la tabla muestra solo las filas
     * que contienen ese texto en alguna columna, sin distinguir mayúsculas.
     *
     * @param tabla         tabla a filtrar
     * @param campoBusqueda campo de texto donde el usuario escribe lo que busca
     */
    protected static void activarBuscador(JTable tabla, JTextField campoBusqueda) {
        TableRowSorter<TableModel> ordenador = new TableRowSorter<>(tabla.getModel());
        tabla.setRowSorter(ordenador);
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(ordenador, campoBusqueda.getText()); }

            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(ordenador, campoBusqueda.getText()); }

            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(ordenador, campoBusqueda.getText()); }
        });
    }

    private static void filtrar(TableRowSorter<TableModel> ordenador, String texto) {
        String buscado = texto.trim();
        ordenador.setRowFilter(buscado.isEmpty() ? null : RowFilter.regexFilter("(?iu)" + Pattern.quote(buscado)));
    }

    /**
     * Devuelve el índice en el modelo de la fila seleccionada, teniendo en cuenta
     * que la tabla puede estar ordenada o filtrada.
     *
     * @param tabla tabla a consultar
     * @return índice en el modelo, o -1 si no hay selección
     */
    protected static int filaSeleccionadaEnModelo(JTable tabla) {
        int filaVista = tabla.getSelectedRow();
        return filaVista < 0 ? -1 : tabla.convertRowIndexToModel(filaVista);
    }
}
