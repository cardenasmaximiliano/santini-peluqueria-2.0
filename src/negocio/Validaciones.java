package negocio;

import excepciones.ValidacionException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

/**
 * Métodos de validación y conversión reutilizados por los gestores y controladores.
 * Todos lanzan {@link ValidacionException} con un mensaje pensado para el usuario.
 */
public final class Validaciones {

    private static final Pattern LETRAS = Pattern.compile("[\\p{L} '.-]+");
    private static final Pattern EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    private static final Pattern TELEFONO = Pattern.compile("[0-9 +()-]+");
    /** Formato de fecha usado en toda la aplicación: dd/mm/aaaa. */
    public static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
    /** Formato para mostrar fechas siempre con dos dígitos: dd/MM/aaaa. */
    public static final DateTimeFormatter FORMATO_FECHA_SALIDA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    /** Formato de hora usado en toda la aplicación: hh:mm (24 horas). */
    public static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("H:mm").withResolverStyle(ResolverStyle.STRICT);

    private Validaciones() {
    }

    /**
     * Quita los espacios del principio y del final; convierte {@code null} en "".
     *
     * @param texto texto a limpiar
     * @return texto sin espacios sobrantes, nunca {@code null}
     */
    public static String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }

    /**
     * Verifica que un campo no esté vacío.
     *
     * @param valor valor ingresado
     * @param campo nombre del campo, para el mensaje
     * @throws ValidacionException si está vacío
     */
    public static void obligatorio(String valor, String campo) throws ValidacionException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException("El campo \"" + campo + "\" es obligatorio.");
        }
    }

    /**
     * Verifica que un texto no supere un largo máximo.
     *
     * @param valor  valor ingresado
     * @param maximo cantidad máxima de caracteres
     * @param campo  nombre del campo, para el mensaje
     * @throws ValidacionException si es demasiado largo
     */
    public static void largoMaximo(String valor, int maximo, String campo) throws ValidacionException {
        if (valor != null && valor.length() > maximo) {
            throw new ValidacionException("El campo \"" + campo + "\" no puede superar los " + maximo + " caracteres.");
        }
    }

    /**
     * @param texto texto a revisar
     * @return {@code true} si solo tiene letras (incluye acentos y ñ), espacios, apóstrofos, puntos o guiones
     */
    public static boolean soloLetras(String texto) {
        return texto != null && LETRAS.matcher(texto).matches();
    }

    /**
     * Un contacto es válido si es un email o un teléfono con al menos 6 dígitos.
     *
     * @param contacto texto ingresado
     * @return {@code true} si tiene formato de email o de teléfono
     */
    public static boolean esContactoValido(String contacto) {
        if (contacto.contains("@")) {
            return EMAIL.matcher(contacto).matches();
        }
        long digitos = contacto.chars().filter(Character::isDigit).count();
        return TELEFONO.matcher(contacto).matches() && digitos >= 6;
    }

    /**
     * Convierte un texto en número entero.
     *
     * @param texto texto ingresado
     * @param campo nombre del campo, para el mensaje
     * @return el número
     * @throws ValidacionException si está vacío o no es un número entero
     */
    public static int aEntero(String texto, String campo) throws ValidacionException {
        obligatorio(texto, campo);
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo \"" + campo + "\" debe ser un número entero (ej: 30).");
        }
    }

    /**
     * Convierte un texto con formato dd/mm/aaaa en fecha. Acepta también d/m/aaaa.
     *
     * @param texto texto ingresado
     * @param campo nombre del campo, para el mensaje
     * @return la fecha
     * @throws ValidacionException si está vacío o no es una fecha válida (ej: 31/02/2026)
     */
    public static LocalDate aFecha(String texto, String campo) throws ValidacionException {
        obligatorio(texto, campo);
        try {
            return LocalDate.parse(texto.trim(), FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            throw new ValidacionException("El campo \"" + campo + "\" debe ser una fecha válida con formato dd/mm/aaaa (ej: 25/11/2026).");
        }
    }

    /**
     * Convierte un texto con formato hh:mm (24 horas) en hora. Acepta también h:mm.
     *
     * @param texto texto ingresado
     * @param campo nombre del campo, para el mensaje
     * @return la hora
     * @throws ValidacionException si está vacío o no es una hora válida
     */
    public static LocalTime aHora(String texto, String campo) throws ValidacionException {
        obligatorio(texto, campo);
        try {
            return LocalTime.parse(texto.trim(), FORMATO_HORA);
        } catch (DateTimeParseException e) {
            throw new ValidacionException("El campo \"" + campo + "\" debe ser una hora válida con formato hh:mm (ej: 14:30).");
        }
    }

    /**
     * Convierte un importe en {@link BigDecimal}. Acepta los formatos habituales en Argentina:
     * "1500", "1500.50", "1500,50", "1.500,50" y "$ 1.500".
     *
     * @param texto texto ingresado
     * @param campo nombre del campo, para el mensaje
     * @return el importe
     * @throws ValidacionException si está vacío o no es un importe válido
     */
    public static BigDecimal aImporte(String texto, String campo) throws ValidacionException {
        obligatorio(texto, campo);
        String limpio = texto.replace("$", "").replace(" ", "").trim();
        if (limpio.contains(",")) {
            limpio = limpio.replace(".", "").replace(",", ".");
        } else if (limpio.matches("\\d{1,3}(\\.\\d{3})+")) {
            limpio = limpio.replace(".", "");
        }
        try {
            return new BigDecimal(limpio);
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo \"" + campo + "\" debe ser un importe válido (ej: 1500 o 1500,50).");
        }
    }
}
