package dao;

import modelo.Servicio;

/**
 * Operaciones de persistencia para {@link Servicio}.
 */
public interface ServicioDAO extends DAO<Servicio, Integer> {

    /**
     * Indica si ya existe otro servicio con el mismo nombre (sin distinguir mayúsculas).
     *
     * @param nombre    nombre a buscar
     * @param idExcluir id del servicio a ignorar (el que se está modificando), o 0
     * @return {@code true} si el nombre ya está en uso
     */
    boolean existeNombre(String nombre, int idExcluir);
}
