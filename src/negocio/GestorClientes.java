package negocio;

import dao.ClienteDAO;
import dao.ClienteDAOImpl;
import dao.TurnoDAO;
import dao.TurnoDAOImpl;
import excepciones.ValidacionException;
import modelo.Cliente;

import java.util.List;

/**
 * Capa de negocio de clientes: valida las reglas antes de delegar la
 * persistencia en el {@link ClienteDAO}.
 */
public class GestorClientes {

    private static final int LARGO_MAXIMO = 100;

    private final ClienteDAO clienteDAO;
    private final TurnoDAO turnoDAO;

    /**
     * Crea el gestor usando las implementaciones JDBC de los DAO.
     */
    public GestorClientes() {
        this(new ClienteDAOImpl(), new TurnoDAOImpl());
    }

    /**
     * Crea el gestor con los DAO indicados (permite reemplazarlos, por ejemplo en pruebas).
     *
     * @param clienteDAO DAO de clientes
     * @param turnoDAO   DAO de turnos, para verificar si el cliente tiene turnos antes de eliminarlo
     */
    public GestorClientes(ClienteDAO clienteDAO, TurnoDAO turnoDAO) {
        this.clienteDAO = clienteDAO;
        this.turnoDAO = turnoDAO;
    }

    /**
     * @return todos los clientes, ordenados por apellido y nombre
     */
    public List<Cliente> listar() {
        return clienteDAO.obtenerTodos();
    }

    /**
     * Valida y guarda un cliente nuevo.
     *
     * @param cliente cliente a registrar
     * @throws ValidacionException si algún dato es inválido
     */
    public void registrar(Cliente cliente) throws ValidacionException {
        validar(cliente);
        clienteDAO.guardar(cliente);
    }

    /**
     * Valida y actualiza un cliente existente.
     *
     * @param cliente cliente con los datos modificados
     * @throws ValidacionException si el cliente no existe o algún dato es inválido
     */
    public void modificar(Cliente cliente) throws ValidacionException {
        if (cliente.esNueva() || clienteDAO.buscarPorId(cliente.getId()) == null) {
            throw new ValidacionException("El cliente que intentás modificar ya no existe.");
        }
        validar(cliente);
        clienteDAO.actualizar(cliente);
    }

    /**
     * Elimina un cliente.
     *
     * @param cliente cliente a eliminar
     * @throws ValidacionException si no se indicó un cliente o si tiene turnos registrados
     */
    public void eliminar(Cliente cliente) throws ValidacionException {
        if (cliente == null || cliente.esNueva()) {
            throw new ValidacionException("Seleccioná un cliente de la tabla para eliminar.");
        }
        int turnos = turnoDAO.contarPorCliente(cliente.getId());
        if (turnos > 0) {
            throw new ValidacionException("No se puede eliminar a " + cliente.getNombreCompleto()
                    + " porque tiene " + turnos + " turno(s) registrado(s). Eliminá esos turnos primero.");
        }
        clienteDAO.eliminar(cliente.getId());
    }

    /**
     * Normaliza (quita espacios sobrantes) y valida los datos del cliente.
     *
     * @param c cliente a validar
     * @throws ValidacionException con un mensaje claro si algo está mal
     */
    private void validar(Cliente c) throws ValidacionException {
        c.setNombre(Validaciones.limpiar(c.getNombre()));
        c.setApellido(Validaciones.limpiar(c.getApellido()));
        c.setContacto(Validaciones.limpiar(c.getContacto()));

        Validaciones.obligatorio(c.getNombre(), "nombre");
        Validaciones.obligatorio(c.getApellido(), "apellido");
        Validaciones.largoMaximo(c.getNombre(), LARGO_MAXIMO, "nombre");
        Validaciones.largoMaximo(c.getApellido(), LARGO_MAXIMO, "apellido");
        Validaciones.largoMaximo(c.getContacto(), LARGO_MAXIMO, "contacto");

        if (!Validaciones.soloLetras(c.getNombre()) || !Validaciones.soloLetras(c.getApellido())) {
            throw new ValidacionException("El nombre y el apellido solo pueden contener letras.");
        }
        if (!c.getContacto().isEmpty() && !Validaciones.esContactoValido(c.getContacto())) {
            throw new ValidacionException("El contacto debe ser un teléfono (mínimo 6 dígitos) o un email válido.");
        }
        if (c.getCategoria() == null) {
            throw new ValidacionException("Seleccioná una categoría.");
        }
    }
}
