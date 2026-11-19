package utnfc.backend.parcial.ParcialAprobado;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParserAlquileresAprobado {
    private final String HEADER = "id,cliente,categoria,sucursal,dias,tarifaDiaria,kmIncluidos,estado";
    private final String HEADER_V2 = "id,cliente,categoria,sucursal,dias,tarifaDiaria,kmIncluidos,estado,kmFinal,diasReales";

    public ResultadoParseoAprobado leer(Path ruta) throws IOException {
        List<Alquiler> alquileres = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        int leidas = 0;
        int descartadas = 0;
        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String header_csv = lector.readLine();
            if (!HEADER.equals(header_csv) && !HEADER_V2.equals(header_csv)) {
                throw new IllegalArgumentException("encabezado incorrecto");
            }
            String linea;
            while ((linea = lector.readLine()) != null) {
                leidas++;
                try {
                    String[] campos = Arrays.stream(linea.split(",", -1))
                            .map(String::strip)
                            .toArray(String[]::new);
                    if (campos.length != 8 && campos.length != 10) {
                        throw new IllegalArgumentException("cantidad de columnas incorrecta");
                    }
                    if (campos[7].equals("CANCELADO")) {
                        descartadas++;
                        continue;
                    }
                    if (!campos[7].equals("ABIERTO") && !campos[7].equals("CERADO")) {
                        throw new IllegalArgumentException("estado desconocido");
                    }
                    alquileres.add(Alquiler.desdeCampos(campos));
                } catch (IllegalArgumentException error) {
                    errores.add("Linea " + (leidas + 1) + ": " + error.getMessage());
                }
            }
        }
        return new ResultadoParseoAprobado(alquileres, leidas, descartadas, errores);
    }
}
