package dao;

import java.util.List;

/**
 * Contrato genérico del patrón DAO (Data Access Object): define las operaciones
 * CRUD que toda entidad persistente debe ofrecer, independientemente de cómo se guarden.
 * <p>
 * Todas las operaciones lanzan {@link excepciones.DAOException} (no verificada)
 * si ocurre un error con la base de datos.
 *
 * @param <T>  tipo de entidad
 * @param <ID> tipo del identificador
 */
public interface DAO<T, ID> {

    /**
     * Inserta una entidad nueva y le asigna el id generado.
     *
     * @param entidad entidad a guardar
     */
    void guardar(T entidad);

    /**
     * Actualiza todos los datos de una entidad existente.
     *
     * @param entidad entidad con los datos nuevos (debe tener id)
     */
    void actualizar(T entidad);

    /**
     * Elimina la entidad con el id indicado.
     *
     * @param id identificador de la entidad
     */
    void eliminar(ID id);

    /**
     * Busca una entidad por su id.
     *
     * @param id identificador
     * @return la entidad, o {@code null} si no existe
     */
    T buscarPorId(ID id);

    /**
     * @return todas las entidades guardadas
     */
    List<T> obtenerTodos();
}
