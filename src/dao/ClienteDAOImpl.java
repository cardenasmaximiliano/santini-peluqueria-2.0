package dao;

import modelo.CategoriaCliente;
import modelo.Cliente;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementación JDBC de {@link ClienteDAO} sobre la tabla {@code clientes}.
 */
public class ClienteDAOImpl extends DAOGenericoJDBC<Cliente> implements ClienteDAO {

    /**
     * Crea el DAO. Cada operación abre su propia conexión mediante {@link ConexionBD}.
     */
    public ClienteDAOImpl() {
    }

    @Override
    protected String getTabla() {
        return "clientes";
    }

    @Override
    protected String[] getColumnas() {
        return new String[]{"nombre", "apellido", "contacto", "categoria", "promociones"};
    }

    @Override
    protected void cargarParametros(PreparedStatement ps, Cliente c) throws SQLException {
        ps.setString(1, c.getNombre());
        ps.setString(2, c.getApellido());
        ps.setString(3, c.getContacto());
        ps.setString(4, c.getCategoria().name());
        ps.setBoolean(5, c.isPromociones());
    }

    @Override
    protected Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("contacto"),
                CategoriaCliente.desdeTexto(rs.getString("categoria")),
                rs.getBoolean("promociones"));
    }

    @Override
    protected String getOrden() {
        return "apellido, nombre";
    }

    @Override
    protected String getNombreEntidad() {
        return "el cliente";
    }
}
