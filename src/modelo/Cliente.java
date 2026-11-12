package modelo;

/**
 * Cliente de la peluquería.
 */
public class Cliente extends Entidad {

    private String nombre;
    private String apellido;
    private String contacto;
    private CategoriaCliente categoria;
    private boolean promociones;

    /**
     * Crea un cliente vacío, con categoría {@link CategoriaCliente#NUEVO}.
     */
    public Cliente() {
        this.categoria = CategoriaCliente.NUEVO;
    }

    /**
     * Crea un cliente con todos sus datos.
     *
     * @param id          identificador (0 si es nuevo)
     * @param nombre      nombre
     * @param apellido    apellido
     * @param contacto    teléfono o email (opcional)
     * @param categoria   categoría del cliente
     * @param promociones si acepta recibir promociones
     */
    public Cliente(int id, String nombre, String apellido, String contacto,
                   CategoriaCliente categoria, boolean promociones) {
        super(id);
        this.nombre = nombre;
        this.apellido = apellido;
        this.contacto = contacto;
        this.categoria = categoria;
        this.promociones = promociones;
    }

    /** @return nombre del cliente */
    public String getNombre() { return nombre; }

    /** @param nombre nombre del cliente */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return apellido del cliente */
    public String getApellido() { return apellido; }

    /** @param apellido apellido del cliente */
    public void setApellido(String apellido) { this.apellido = apellido; }

    /** @return teléfono o email (puede ser vacío) */
    public String getContacto() { return contacto; }

    /** @param contacto teléfono o email */
    public void setContacto(String contacto) { this.contacto = contacto; }

    /** @return categoría del cliente */
    public CategoriaCliente getCategoria() { return categoria; }

    /** @param categoria categoría del cliente */
    public void setCategoria(CategoriaCliente categoria) { this.categoria = categoria; }

    /** @return {@code true} si acepta recibir promociones */
    public boolean isPromociones() { return promociones; }

    /** @param promociones si acepta recibir promociones */
    public void setPromociones(boolean promociones) { this.promociones = promociones; }

    /**
     * @return "Nombre Apellido"
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * @return nombre completo, usado al mostrar el cliente en un combo
     */
    @Override
    public String toString() {
        return getNombreCompleto();
    }
}
