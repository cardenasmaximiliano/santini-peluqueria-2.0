package negocio;

import modelo.Turno;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exporta turnos a un archivo CSV que se puede abrir directamente con Excel o LibreOffice.
 * <p>
 * Usa punto y coma como separador y coma decimal (configuración regional de Argentina),
 * y escribe el archivo en UTF-8 con BOM para que Excel muestre bien los acentos.
 */
public final class ExportadorCSV {

    private static final String SEPARADOR = ";";
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private ExportadorCSV() {
    }

    /**
     * Escribe los turnos en el archivo indicado (si existe, lo reemplaza).
     *
     * @param turnos  turnos a exportar
     * @param destino archivo de salida
     * @throws IOException si no se puede escribir el archivo (por ejemplo, si está abierto en Excel)
     */
    public static void exportarTurnos(List<Turno> turnos, Path destino) throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            out.write('﻿');
            out.write(String.join(SEPARADOR, "ID", "Fecha", "Hora", "Fin", "Cliente", "Contacto",
                    "Servicio", "Duración (min)", "Precio", "Estado"));
            out.newLine();
            for (Turno t : turnos) {
                out.write(String.join(SEPARADOR,
                        String.valueOf(t.getId()),
                        t.getFecha().format(FECHA),
                        t.getHora().format(HORA),
                        t.getHoraFin().format(HORA),
                        campo(t.getCliente().getNombreCompleto()),
                        campo(t.getCliente().getContacto()),
                        campo(t.getServicio().getNombre()),
                        String.valueOf(t.getServicio().getDuracionMinutos()),
                        t.getServicio().getPrecio().toPlainString().replace('.', ','),
                        t.getEstado().toString()));
                out.newLine();
            }
        }
    }

    /**
     * Escapa un texto para CSV: si contiene el separador, comillas o saltos de línea,
     * lo encierra entre comillas y duplica las comillas internas.
     */
    private static String campo(String texto) {
        if (texto == null) {
            return "";
        }
        if (texto.contains(SEPARADOR) || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }
}
