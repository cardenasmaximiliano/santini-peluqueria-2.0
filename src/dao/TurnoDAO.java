package dao;

import modelo.Turno;

import java.time.LocalDate;
import java.util.List;

/**
 * Operaciones de persistencia para {@link Turno}.
 */
public interface TurnoDAO extends DAO<Turno, Integer> {

    /**
     * @param fecha día a consultar
     * @return los turnos de ese día (de cualquier estado), ordenados por hora
     */
    List<Turno> buscarPorFecha(LocalDate fecha);

    /**
     * @param idCliente id del cliente
     * @return cantidad de turnos registrados para ese cliente
     */
    int contarPorCliente(int idCliente);

    /**
     * @param idServicio id del servicio
     * @return cantidad de turnos registrados para ese servicio
     */
    int contarPorServicio(int idServicio);
}
