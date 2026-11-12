package modelo;

import java.math.BigDecimal;

/**
 * Servicio ofrecido por la peluquería (corte, color, peinado, etc.).
 * El precio se maneja con {@link BigDecimal} para evitar errores de redondeo con dinero.
 */
public class Servicio extends Entidad {

    private String nombre;
    private int duracionMinutos;
    private BigDecimal precio;

    /**
     * Crea un servicio vacío.
     */
    public Servicio() {
    }

    /**
     * Crea un servicio con todos sus datos.
     *
     * @param id              identificador (0 si es nuevo)
     * @param nombre          nombre del servicio
     * @param duracionMinutos duración en minutos
     * @param precio          precio en pesos
     */
    public Servicio(int id, String nombre, int duracionMinutos, BigDecimal precio) {
        super(id);
        this.nombre = nombre;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
    }

    /** @return nombre del servicio */
    public String getNombre() { return nombre; }

    /** @param nombre nombre del servicio */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return duración en minutos */
    public int getDuracionMinutos() { return duracionMinutos; }

    /** @param duracionMinutos duración en minutos */
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    /** @return precio en pesos */
    public BigDecimal getPrecio() { return precio; }

    /** @param precio precio en pesos */
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    /**
     * @return "Nombre (N min)", usado al mostrar el servicio en un combo
     */
    @Override
    public String toString() {
        return nombre + " (" + duracionMinutos + " min)";
    }
}
