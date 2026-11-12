package dao;

import modelo.CategoriaCliente;
import modelo.Cliente;
import modelo.EstadoTurno;
import modelo.Servicio;
import modelo.Turno;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Implementación JDBC de {@link TurnoDAO} sobre la tabla {@code turnos}.
 * <p>
 * En la tabla solo se guardan los ids de cliente y servicio (claves foráneas);
 * al leer se hace un JOIN para reconstruir los objetos {@link Cliente} y {@link Servicio} completos.
 */
public class TurnoDAOImpl extends DAOGenericoJDBC<Turno> implements TurnoDAO {

    private static final String CONSULTA_CON_JOIN =
            "SELECT t.id, t.fecha, t.hora, t.estado,"
                    + " c.id AS c_id, c.nombre AS c_nombre, c.apellido AS c_apellido,"
                    + " c.contacto AS c_contacto, c.categoria AS c_categoria, c.promociones AS c_promociones,"
                    + " s.id AS s_id, s.nombre AS s_nombre, s.duracion AS s_duracion, s.precio AS s_precio"
                    + " FROM turnos t"
                    + " JOIN clientes c ON c.id = t.cliente_id"
                    + " JOIN servicios s ON s.id = t.servicio_id";

    /**
     * Crea el DAO. Cada operación abre su propia conexión mediante {@link ConexionBD}.
     */
    public TurnoDAOImpl() {
    }

    @Override
    protected String getTabla() {
        return "turnos";
    }

    @Override
    protected String[] getColumnas() {
        return new String[]{"cliente_id", "servicio_id", "fecha", "hora", "estado"};
    }

    @Override
    protected void cargarParametros(PreparedStatement ps, Turno t) throws SQLException {
        ps.setInt(1, t.getCliente().getId());
        ps.setInt(2, t.getServicio().getId());
        ps.setObject(3, t.getFecha());
        ps.setObject(4, t.getHora());
        ps.setString(5, t.getEstado().name());
    }

    @Override
    protected Turno mapear(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getInt("c_id"),
                rs.getString("c_nombre"),
                rs.getString("c_apellido"),
                rs.getString("c_contacto"),
                CategoriaCliente.desdeTexto(rs.getString("c_categoria")),
                rs.getBoolean("c_promociones"));
        Servicio servicio = new Servicio(
                rs.getInt("s_id"),
                rs.getString("s_nombre"),
                rs.getInt("s_duracion"),
                rs.getBigDecimal("s_precio"));
        return new Turno(
                rs.getInt("id"),
                cliente,
                servicio,
                rs.getObject("fecha", LocalDate.class),
                rs.getObject("hora", LocalTime.class),
                EstadoTurno.desdeTexto(rs.getString("estado")));
    }

    @Override
    protected String getConsultaBase() {
        return CONSULTA_CON_JOIN;
    }

    @Override
    protected String getColumnaId() {
        return "t.id";
    }

    @Override
    protected String getOrden() {
        return "t.fecha, t.hora";
    }

    @Override
    protected String getNombreEntidad() {
        return "el turno";
    }

    @Override
    public List<Turno> buscarPorFecha(LocalDate fecha) {
        return consultar(CONSULTA_CON_JOIN + " WHERE t.fecha = ? ORDER BY t.hora", fecha);
    }

    @Override
    public int contarPorCliente(int idCliente) {
        return contar("SELECT COUNT(*) FROM turnos WHERE cliente_id = ?", idCliente);
    }

    @Override
    public int contarPorServicio(int idServicio) {
        return contar("SELECT COUNT(*) FROM turnos WHERE servicio_id = ?", idServicio);
    }
}
