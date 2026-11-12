package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Turno reservado: combina un {@link Cliente} con un {@link Servicio} en una fecha y hora.
 * <p>
 * Es la entidad que relaciona a las otras dos. Su hora de finalización se calcula
 * a partir de la duración del servicio, lo que permite detectar superposiciones.
 */
public class Turno extends Entidad {

    private Cliente cliente;
    private Servicio servicio;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoTurno estado;

    /**
     * Crea un turno vacío en estado {@link EstadoTurno#PENDIENTE}.
     */
    public Turno() {
        this.estado = EstadoTurno.PENDIENTE;
    }

    /**
     * Crea un turno con todos sus datos.
     *
     * @param id       identificador (0 si es nuevo)
     * @param cliente  cliente que reserva
     * @param servicio servicio reservado
     * @param fecha    día del turno
     * @param hora     hora de inicio
     * @param estado   estado del turno
     */
    public Turno(int id, Cliente cliente, Servicio servicio, LocalDate fecha, LocalTime hora, EstadoTurno estado) {
        super(id);
        this.cliente = cliente;
        this.servicio = servicio;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }

    /** @return cliente que reservó el turno */
    public Cliente getCliente() { return cliente; }

    /** @param cliente cliente que reserva el turno */
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    /** @return servicio reservado */
    public Servicio getServicio() { return servicio; }

    /** @param servicio servicio reservado */
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    /** @return día del turno */
    public LocalDate getFecha() { return fecha; }

    /** @param fecha día del turno */
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    /** @return hora de inicio */
    public LocalTime getHora() { return hora; }

    /** @param hora hora de inicio */
    public void setHora(LocalTime hora) { this.hora = hora; }

    /** @return estado del turno */
    public EstadoTurno getEstado() { return estado; }

    /** @param estado estado del turno */
    public void setEstado(EstadoTurno estado) { this.estado = estado; }

    /**
     * @return hora de finalización = hora de inicio + duración del servicio
     */
    public LocalTime getHoraFin() {
        return hora.plusMinutes(servicio.getDuracionMinutos());
    }

    /**
     * @return fecha y hora de inicio juntas
     */
    public LocalDateTime getInicio() {
        return LocalDateTime.of(fecha, hora);
    }

    /**
     * Indica si este turno y otro ocupan al mismo tiempo la agenda.
     * Los turnos cancelados no ocupan horario, por eso nunca se superponen.
     *
     * @param otro turno a comparar
     * @return {@code true} si son el mismo día y sus horarios se pisan
     */
    public boolean seSuperponeCon(Turno otro) {
        if (!estado.ocupaHorario() || !otro.estado.ocupaHorario() || !fecha.equals(otro.fecha)) {
            return false;
        }
        return hora.isBefore(otro.getHoraFin()) && otro.hora.isBefore(getHoraFin());
    }

    /**
     * @return descripción breve: "cliente - servicio dd/mm hh:mm"
     */
    @Override
    public String toString() {
        return cliente + " - " + servicio.getNombre() + " " + fecha + " " + hora;
    }
}
