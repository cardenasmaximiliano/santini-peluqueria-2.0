package dao;

import modelo.Servicio;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementación JDBC de {@link ServicioDAO} sobre la tabla {@code servicios}.
 */
public class ServicioDAOImpl extends DAOGenericoJDBC<Servicio> implements ServicioDAO {

    /**
     * Crea el DAO. Cada operación abre su propia conexión mediante {@link ConexionBD}.
     */
    public ServicioDAOImpl() {
    }

    @Override
    protected String getTabla() {
        return "servicios";
    }

    @Override
    protected String[] getColumnas() {
        return new String[]{"nombre", "duracion", "precio"};
    }

    @Override
    protected void cargarParametros(PreparedStatement ps, Servicio s) throws SQLException {
        ps.setString(1, s.getNombre());
        ps.setInt(2, s.getDuracionMinutos());
        ps.setBigDecimal(3, s.getPrecio());
    }

    @Override
    protected Servicio mapear(ResultSet rs) throws SQLException {
        return new Servicio(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getInt("duracion"),
                rs.getBigDecimal("precio"));
    }

    @Override
    protected String getOrden() {
        return "nombre";
    }

    @Override
    protected String getNombreEntidad() {
        return "el servicio";
    }

    @Override
    public boolean existeNombre(String nombre, int idExcluir) {
        return contar("SELECT COUNT(*) FROM servicios WHERE LOWER(nombre) = LOWER(?) AND id <> ?",
                nombre, idExcluir) > 0;
    }
}
