package negocio;

import dao.TurnoDAO;
import dao.TurnoDAOImpl;
import excepciones.ValidacionException;
import modelo.EstadoTurno;
import modelo.Turno;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Capa de negocio de turnos. Además de validar los datos, aplica las reglas de la agenda:
 * <ul>
 *     <li>no se reserva en el pasado;</li>
 *     <li>el turno debe empezar y terminar dentro del horario de atención;</li>
 *     <li>no puede superponerse con otro turno activo;</li>
 *     <li>un turno cancelado no se puede modificar.</li>
 * </ul>
 */
public class GestorTurnos {

    /** Hora de apertura de la peluquería. */
    public static final LocalTime APERTURA = LocalTime.of(9, 0);
    /** Hora de cierre: los turnos deben terminar a esta hora como máximo. */
    public static final LocalTime CIERRE = LocalTime.of(20, 0);

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    private final TurnoDAO turnoDAO;
    private final Clock reloj;

    /**
     * Crea el gestor usando la implementación JDBC del DAO y la hora del sistema.
     */
    public GestorTurnos() {
        this(new TurnoDAOImpl(), Clock.systemDefaultZone());
    }

    /**
     * Crea el gestor con el DAO y el reloj indicados (permite reemplazarlos, por ejemplo en pruebas).
     *
     * @param turnoDAO DAO a utilizar
     * @param reloj    reloj para saber la fecha y hora actual
     */
    public GestorTurnos(TurnoDAO turnoDAO, Clock reloj) {
        this.turnoDAO = turnoDAO;
        this.reloj = reloj;
    }

    /**
     * @return todos los turnos, ordenados por fecha y hora
     */
    public List<Turno> listar() {
        return turnoDAO.obtenerTodos();
    }

    /**
     * Valida y registra un turno nuevo en estado {@link EstadoTurno#PENDIENTE}.
     *
     * @param turno turno a reservar
     * @throws ValidacionException si falta algún dato, es en el pasado, está fuera de horario o se superpone
     */
    public void reservar(Turno turno) throws ValidacionException {
        turno.setEstado(EstadoTurno.PENDIENTE);
        validarDatos(turno);
        validarNoEsPasado(turno);
        validarHorarioDeAtencion(turno);
        validarSinSuperposicion(turno);
        turnoDAO.guardar(turno);
    }

    /**
     * Valida y actualiza un turno existente.
     * Si no cambian la fecha ni la hora, se permite actualizar turnos pasados
     * (por ejemplo, para marcarlos como realizados).
     *
     * @param turno turno con los datos modificados
     * @throws ValidacionException si el turno no existe, está cancelado o los nuevos datos no son válidos
     */
    public void modificar(Turno turno) throws ValidacionException {
        Turno original = turno.esNueva() ? null : turnoDAO.buscarPorId(turno.getId());
        if (original == null) {
            throw new ValidacionException("El turno que intentás modificar ya no existe.");
        }
        if (original.getEstado() == EstadoTurno.CANCELADO) {
            throw new ValidacionException("Un turno cancelado no se puede modificar. Reservá uno nuevo.");
        }
        validarDatos(turno);
        boolean cambiaHorario = !original.getFecha().equals(turno.getFecha())
                || !original.getHora().equals(turno.getHora());
        if (cambiaHorario) {
            validarNoEsPasado(turno);
        }
        validarHorarioDeAtencion(turno);
        validarSinSuperposicion(turno);
        turnoDAO.actualizar(turno);
    }

    /**
     * Cancela un turno: queda registrado pero libera el horario.
     *
     * @param turno turno a cancelar
     * @throws ValidacionException si no se indicó un turno o ya estaba cancelado o realizado
     */
    public void cancelar(Turno turno) throws ValidacionException {
        if (turno == null || turno.esNueva()) {
            throw new ValidacionException("Seleccioná un turno de la tabla para cancelar.");
        }
        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new ValidacionException("El turno ya estaba cancelado.");
        }
        if (turno.getEstado() == EstadoTurno.REALIZADO) {
            throw new ValidacionException("No se puede cancelar un turno que ya fue realizado.");
        }
        turno.setEstado(EstadoTurno.CANCELADO);
        turnoDAO.actualizar(turno);
    }

    /**
     * Elimina definitivamente un turno.
     *
     * @param turno turno a eliminar
     * @throws ValidacionException si no se indicó un turno
     */
    public void eliminar(Turno turno) throws ValidacionException {
        if (turno == null || turno.esNueva()) {
            throw new ValidacionException("Seleccioná un turno de la tabla para eliminar.");
        }
        turnoDAO.eliminar(turno.getId());
    }

    private void validarDatos(Turno t) throws ValidacionException {
        if (t.getCliente() == null) {
            throw new ValidacionException("Seleccioná un cliente.");
        }
        if (t.getServicio() == null) {
            throw new ValidacionException("Seleccioná un servicio.");
        }
        if (t.getFecha() == null || t.getHora() == null) {
            throw new ValidacionException("La fecha y la hora son obligatorias.");
        }
        if (t.getEstado() == null) {
            throw new ValidacionException("Seleccioná un estado.");
        }
    }

    private void validarNoEsPasado(Turno t) throws ValidacionException {
        if (t.getInicio().isBefore(LocalDateTime.now(reloj))) {
            throw new ValidacionException("No se puede reservar un turno en una fecha u hora que ya pasó.");
        }
    }

    private void validarHorarioDeAtencion(Turno t) throws ValidacionException {
        String horario = "El horario de atención es de " + APERTURA.format(HH_MM) + " a " + CIERRE.format(HH_MM) + ".";
        if (t.getHora().isBefore(APERTURA)) {
            throw new ValidacionException(horario + " El turno no puede empezar antes de las "
                    + APERTURA.format(HH_MM) + ".");
        }
        if (t.getHoraFin().isAfter(CIERRE) || t.getHoraFin().isBefore(t.getHora())) {
            throw new ValidacionException(horario + " Este turno terminaría a las "
                    + t.getHoraFin().format(HH_MM) + ".");
        }
    }

    private void validarSinSuperposicion(Turno t) throws ValidacionException {
        for (Turno otro : turnoDAO.buscarPorFecha(t.getFecha())) {
            if (otro.getId() != t.getId() && t.seSuperponeCon(otro)) {
                throw new ValidacionException("El horario se superpone con el turno de "
                        + otro.getCliente().getNombreCompleto() + " (" + otro.getServicio().getNombre() + ", "
                        + otro.getHora().format(HH_MM) + " a " + otro.getHoraFin().format(HH_MM) + ").");
            }
        }
    }
}
