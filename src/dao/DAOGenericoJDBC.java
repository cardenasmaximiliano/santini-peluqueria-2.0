package dao;

import excepciones.DAOException;
import modelo.Entidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementación base y reutilizable del {@link DAO} usando JDBC.
 * <p>
 * Aplica el patrón <i>Template Method</i>: esta clase resuelve todo lo común
 * (abrir/cerrar conexiones, armar los INSERT/UPDATE/DELETE, traducir errores)
 * y cada DAO concreto solo indica su tabla, sus columnas y cómo convertir
 * una fila en un objeto.
 *
 * @param <T> tipo de entidad que maneja
 */
public abstract class DAOGenericoJDBC<T extends Entidad> implements DAO<T, Integer> {

    /**
     * Constructor para las subclases.
     */
    protected DAOGenericoJDBC() {
    }

    /**
     * @return nombre de la tabla en la base de datos
     */
    protected abstract String getTabla();

    /**
     * @return columnas a insertar/actualizar, sin el id y en el mismo orden que
     * {@link #cargarParametros(PreparedStatement, Entidad)}
     */
    protected abstract String[] getColumnas();

    /**
     * Carga los valores de la entidad en la sentencia, en el orden de {@link #getColumnas()}.
     *
     * @param ps      sentencia a completar
     * @param entidad entidad de la que se toman los valores
     * @throws SQLException si falla la carga de algún parámetro
     */
    protected abstract void cargarParametros(PreparedStatement ps, T entidad) throws SQLException;

    /**
     * Convierte la fila actual del {@link ResultSet} en una entidad.
     *
     * @param rs resultado posicionado en una fila
     * @return entidad construida
     * @throws SQLException si falla la lectura de alguna columna
     */
    protected abstract T mapear(ResultSet rs) throws SQLException;

    /**
     * @return nombre de la entidad con artículo, para los mensajes de error (ej: "el cliente")
     */
    protected abstract String getNombreEntidad();

    /**
     * Consulta base para listar y buscar. Las subclases la pueden redefinir para hacer JOIN.
     *
     * @return sentencia SELECT sin WHERE ni ORDER BY
     */
    protected String getConsultaBase() {
        return "SELECT * FROM " + getTabla();
    }

    /**
     * @return columna id calificada como aparece en {@link #getConsultaBase()}
     */
    protected String getColumnaId() {
        return "id";
    }

    /**
     * @return criterio de orden para {@link #obtenerTodos()}
     */
    protected String getOrden() {
        return getColumnaId();
    }

    @Override
    public void guardar(T entidad) {
        String columnas = String.join(", ", getColumnas());
        String signos = String.join(", ", Collections.nCopies(getColumnas().length, "?"));
        String sql = "INSERT INTO " + getTabla() + " (" + columnas + ") VALUES (" + signos + ")";

        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cargarParametros(ps, entidad);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    entidad.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo guardar " + getNombreEntidad() + ".", e);
        }
    }

    @Override
    public void actualizar(T entidad) {
        String asignaciones = String.join(" = ?, ", getColumnas()) + " = ?";
        String sql = "UPDATE " + getTabla() + " SET " + asignaciones + " WHERE id = ?";

        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            cargarParametros(ps, entidad);
            ps.setInt(getColumnas().length + 1, entidad.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("No se pudo actualizar " + getNombreEntidad() + ".", e);
        }
    }

    @Override
    public void eliminar(Integer id) {
        String sql = "DELETE FROM " + getTabla() + " WHERE id = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DAOException("No se puede eliminar " + getNombreEntidad()
                    + " porque tiene turnos asociados.", e);
        } catch (SQLException e) {
            throw new DAOException("No se pudo eliminar " + getNombreEntidad() + ".", e);
        }
    }

    @Override
    public T buscarPorId(Integer id) {
        List<T> resultado = consultar(getConsultaBase() + " WHERE " + getColumnaId() + " = ?", id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    @Override
    public List<T> obtenerTodos() {
        return consultar(getConsultaBase() + " ORDER BY " + getOrden());
    }

    /**
     * Ejecuta una consulta parametrizada y convierte cada fila con {@link #mapear(ResultSet)}.
     * Las subclases la usan para sus búsquedas propias.
     *
     * @param sql        sentencia SELECT con signos {@code ?}
     * @param parametros valores para cada {@code ?}, en orden
     * @return lista de entidades encontradas (vacía si no hay resultados)
     */
    protected List<T> consultar(String sql, Object... parametros) {
        List<T> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo consultar " + getNombreEntidad() + ".", e);
        }
        return lista;
    }

    /**
     * Ejecuta una consulta {@code SELECT COUNT(*)} parametrizada.
     *
     * @param sql        sentencia que devuelve un único número
     * @param parametros valores para cada {@code ?}, en orden
     * @return el número devuelto por la consulta
     */
    protected int contar(String sql, Object... parametros) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo consultar " + getNombreEntidad() + ".", e);
        }
    }
}
