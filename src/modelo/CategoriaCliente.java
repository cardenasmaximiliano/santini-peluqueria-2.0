package modelo;

/**
 * Categorías posibles de un cliente según su frecuencia de visita.
 */
public enum CategoriaCliente {
    /** Cliente que viene por primera vez. */
    NUEVO("Nuevo"),
    /** Cliente que viene de vez en cuando. */
    HABITUAL("Habitual"),
    /** Cliente que viene seguido. */
    FRECUENTE("Frecuente"),
    /** Cliente preferencial. */
    VIP("VIP");

    private final String descripcion;

    CategoriaCliente(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Convierte el texto guardado en la base de datos a una categoría.
     * Acepta tanto el nombre de la constante ("FRECUENTE") como la descripción ("Frecuente").
     * Si el texto es nulo o no se reconoce, devuelve {@link #NUEVO}.
     *
     * @param texto valor leído de la base de datos
     * @return la categoría correspondiente
     */
    public static CategoriaCliente desdeTexto(String texto) {
        if (texto != null) {
            for (CategoriaCliente c : values()) {
                if (c.name().equalsIgnoreCase(texto.trim()) || c.descripcion.equalsIgnoreCase(texto.trim())) {
                    return c;
                }
            }
        }
        return NUEVO;
    }

    /**
     * @return descripción legible, usada en combos y tablas
     */
    @Override
    public String toString() {
        return descripcion;
    }
}
