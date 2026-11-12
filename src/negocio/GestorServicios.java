package negocio;

import dao.ServicioDAO;
import dao.ServicioDAOImpl;
import dao.TurnoDAO;
import dao.TurnoDAOImpl;
import excepciones.ValidacionException;
import modelo.Servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Capa de negocio de servicios: valida las reglas antes de delegar la
 * persistencia en el {@link ServicioDAO}.
 */
public class GestorServicios {

    /** Duración mínima de un servicio, en minutos. */
    public static final int DURACION_MINIMA = 5;
    /** Duración máxima de un servicio, en minutos (8 horas). */
    public static final int DURACION_MAXIMA = 480;
    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("99999999.99");

    private final ServicioDAO servicioDAO;
    private final TurnoDAO turnoDAO;

    /**
     * Crea el gestor usando las implementaciones JDBC de los DAO.
     */
    public GestorServicios() {
        this(new ServicioDAOImpl(), new TurnoDAOImpl());
    }

    /**
     * Crea el gestor con los DAO indicados (permite reemplazarlos, por ejemplo en pruebas).
     *
     * @param servicioDAO DAO de servicios
     * @param turnoDAO    DAO de turnos, para verificar si el servicio tiene turnos antes de eliminarlo
     */
    public GestorServicios(ServicioDAO servicioDAO, TurnoDAO turnoDAO) {
        this.servicioDAO = servicioDAO;
        this.turnoDAO = turnoDAO;
    }

    /**
     * @return todos los servicios, ordenados por nombre
     */
    public List<Servicio> listar() {
        return servicioDAO.obtenerTodos();
    }

    /**
     * Valida y guarda un servicio nuevo.
     *
     * @param servicio servicio a registrar
     * @throws ValidacionException si algún dato es inválido o el nombre ya existe
     */
    public void registrar(Servicio servicio) throws ValidacionException {
        validar(servicio);
        servicioDAO.guardar(servicio);
    }

    /**
     * Valida y actualiza un servicio existente.
     *
     * @param servicio servicio con los datos modificados
     * @throws ValidacionException si el servicio no existe o algún dato es inválido
     */
    public void modificar(Servicio servicio) throws ValidacionException {
        if (servicio.esNueva() || servicioDAO.buscarPorId(servicio.getId()) == null) {
            throw new ValidacionException("El servicio que intentás modificar ya no existe.");
        }
        validar(servicio);
        servicioDAO.actualizar(servicio);
    }

    /**
     * Elimina un servicio.
     *
     * @param servicio servicio a eliminar
     * @throws ValidacionException si no se indicó un servicio o si tiene turnos registrados
     */
    public void eliminar(Servicio servicio) throws ValidacionException {
        if (servicio == null || servicio.esNueva()) {
            throw new ValidacionException("Seleccioná un servicio de la tabla para eliminar.");
        }
        int turnos = turnoDAO.contarPorServicio(servicio.getId());
        if (turnos > 0) {
            throw new ValidacionException("No se puede eliminar el servicio \"" + servicio.getNombre()
                    + "\" porque tiene " + turnos + " turno(s) registrado(s). Eliminá esos turnos primero.");
        }
        servicioDAO.eliminar(servicio.getId());
    }

    private void validar(Servicio s) throws ValidacionException {
        s.setNombre(Validaciones.limpiar(s.getNombre()));
        Validaciones.obligatorio(s.getNombre(), "nombre");
        Validaciones.largoMaximo(s.getNombre(), 100, "nombre");

        if (s.getDuracionMinutos() < DURACION_MINIMA || s.getDuracionMinutos() > DURACION_MAXIMA) {
            throw new ValidacionException("La duración debe estar entre " + DURACION_MINIMA
                    + " y " + DURACION_MAXIMA + " minutos.");
        }
        if (s.getPrecio() == null || s.getPrecio().signum() <= 0) {
            throw new ValidacionException("El precio debe ser mayor a cero.");
        }
        if (s.getPrecio().compareTo(PRECIO_MAXIMO) > 0) {
            throw new ValidacionException("El precio es demasiado alto.");
        }
        s.setPrecio(s.getPrecio().setScale(2, RoundingMode.HALF_UP));

        if (servicioDAO.existeNombre(s.getNombre(), s.getId())) {
            throw new ValidacionException("Ya existe un servicio llamado \"" + s.getNombre() + "\".");
        }
    }
}
