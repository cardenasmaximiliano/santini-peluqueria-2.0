package modelo;

/**
 * Estados posibles de un turno a lo largo de su vida.
 */
public enum EstadoTurno {
    /** Reservado, falta confirmar. */
    PENDIENTE("Pendiente"),
    /** El cliente confirmó que asiste. */
    CONFIRMADO("Confirmado"),
    /** Anulado: libera el horario. */
    CANCELADO("Cancelado"),
    /** El servicio ya se prestó. */
    REALIZADO("Realizado");

    private final String descripcion;

    EstadoTurno(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Convierte el texto guardado en la base de datos a un estado.
     * Si el texto es nulo o no se reconoce, devuelve {@link #PENDIENTE}.
     *
     * @param texto valor leído de la base de datos
     * @return el estado correspondiente
     */
    public static EstadoTurno desdeTexto(String texto) {
        if (texto != null) {
            for (EstadoTurno e : values()) {
                if (e.name().equalsIgnoreCase(texto.trim()) || e.descripcion.equalsIgnoreCase(texto.trim())) {
                    return e;
                }
            }
        }
        return PENDIENTE;
    }

    /**
     * @return {@code true} si el turno ocupa el horario (no está cancelado)
     */
    public boolean ocupaHorario() {
        return this != CANCELADO;
    }

    /**
     * @return descripción legible, usada en combos y tablas
     */
    @Override
    public String toString() {
        return descripcion;
    }
}
