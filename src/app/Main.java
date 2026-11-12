package app;

import dao.InicializadorBD;
import excepciones.DAOException;
import vista.VentanaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicación "Peluquería Santini".
 */
public final class Main {

    private Main() {
    }

    /**
     * Prepara la base de datos y abre la ventana principal.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((hilo, error) -> {
            error.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ocurrió un error inesperado:\n" + error,
                    "Error", JOptionPane.ERROR_MESSAGE);
        });

        SwingUtilities.invokeLater(() -> {
            aplicarAspectoDelSistema();
            try {
                InicializadorBD.crearTablasSiNoExisten();
            } catch (DAOException e) {
                JOptionPane.showMessageDialog(null, e.getMensajeCompleto(),
                        "Error de base de datos", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            new VentanaPrincipal().setVisible(true);
        });
    }

    private static void aplicarAspectoDelSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el aspecto del sistema: " + e.getMessage());
        }
    }
}
