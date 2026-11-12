package vista;

import controlador.ClienteControlador;
import controlador.ControladorBase;
import controlador.ServicioControlador;
import controlador.TurnoControlador;
import negocio.GestorClientes;
import negocio.GestorServicios;
import negocio.GestorTurnos;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * Ventana principal de la aplicación.
 * <p>
 * Contiene una botonera superior y un panel central con {@link CardLayout}
 * donde se alterna entre las pantallas de Clientes, Servicios y Turnos.
 * El diseño está en {@code VentanaPrincipal.form}.
 */
public final class VentanaPrincipal extends JFrame {

    private static final String CLIENTES = "CLIENTES";
    private static final String SERVICIOS = "SERVICIOS";
    private static final String TURNOS = "TURNOS";

    /** Panel raíz de la ventana (definido en el .form). */
    private JPanel panelPrincipal;
    /** Panel central con CardLayout donde se alternan las pantallas. */
    private JPanel panelContenido;
    /** Botón que muestra la pantalla de clientes. */
    private JButton botonClientes;
    /** Botón que muestra la pantalla de servicios. */
    private JButton botonServicios;
    /** Botón que muestra la pantalla de turnos. */
    private JButton botonTurnos;

    /** Controlador de cada pantalla, para refrescarla al mostrarla. */
    private Map<String, ControladorBase<?>> controladores;

    /**
     * Crea la ventana, sus pantallas y los controladores de cada una.
     */
    public VentanaPrincipal() {
        setTitle("Peluquería Santini - Sistema de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panelPrincipal);
        setSize(900, 600);
        setLocationRelativeTo(null);

        controladores = new HashMap<>();

        GestorClientes gestorClientes = new GestorClientes();
        GestorServicios gestorServicios = new GestorServicios();
        GestorTurnos gestorTurnos = new GestorTurnos();

        PanelCliente panelCliente = new PanelCliente();
        controladores.put(CLIENTES, new ClienteControlador(panelCliente, gestorClientes));
        panelContenido.add(panelCliente.getPanelPrincipal(), CLIENTES);

        PanelServicios panelServicios = new PanelServicios();
        controladores.put(SERVICIOS, new ServicioControlador(panelServicios, gestorServicios));
        panelContenido.add(panelServicios.getPanelPrincipal(), SERVICIOS);

        PanelTurnos panelTurnos = new PanelTurnos();
        controladores.put(TURNOS, new TurnoControlador(panelTurnos, gestorTurnos, gestorClientes, gestorServicios));
        panelContenido.add(panelTurnos.getPanelPrincipal(), TURNOS);

        botonClientes.addActionListener(e -> mostrar(CLIENTES));
        botonServicios.addActionListener(e -> mostrar(SERVICIOS));
        botonTurnos.addActionListener(e -> mostrar(TURNOS));
    }

    /**
     * Muestra la pantalla indicada y refresca sus datos desde la base.
     *
     * @param nombre nombre de la pantalla ({@code CLIENTES}, {@code SERVICIOS} o {@code TURNOS})
     */
    private void mostrar(String nombre) {
        ControladorBase<?> controlador = controladores.get(nombre);
        if (controlador != null) {
            controlador.actualizarVista();
        }
        ((CardLayout) panelContenido.getLayout()).show(panelContenido, nombre);
    }
}
