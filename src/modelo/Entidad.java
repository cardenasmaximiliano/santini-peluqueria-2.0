package modelo;

/**
 * Clase base abstracta de todas las entidades persistentes del sistema.
 * <p>
 * Centraliza el identificador y la comparación por identidad, de modo que
 * {@link Cliente}, {@link Servicio} y {@link Turno} lo heredan sin repetir código.
 */
public abstract class Entidad {

    private int id;

    /**
     * Crea una entidad nueva, todavía no guardada (id = 0).
     */
    protected Entidad() {
    }

    /**
     * Crea una entidad con un identificador conocido.
     *
     * @param id identificador asignado por la base de datos
     */
    protected Entidad(int id) {
        this.id = id;
    }

    /**
     * @return identificador de la entidad (0 si todavía no fue guardada)
     */
    public int getId() {
        return id;
    }

    /**
     * @param id identificador asignado por la base de datos
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Indica si la entidad todavía no fue guardada en la base de datos.
     *
     * @return {@code true} si el id es 0
     */
    public boolean esNueva() {
        return id == 0;
    }

    /**
     * Dos entidades son iguales si son de la misma clase y tienen el mismo id.
     *
     * @param obj objeto a comparar
     * @return {@code true} si representan el mismo registro
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return id == ((Entidad) obj).id;
    }

    /**
     * Calculado a partir del id, consistente con {@link #equals(Object)}.
     *
     * @return código hash de la entidad
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
