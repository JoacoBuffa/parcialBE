package utnfc.backend.parcial;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParserAlquileres {
    private static final String ENCABEZADO_INICIAL =
            "id,cliente,categoria,sucursal,dias,tarifaDiaria,kmIncluidos,estado";
    private static final String ENCABEZADO_EVOLUCIONADO = ENCABEZADO_INICIAL + ",kmFinal,diasReales";

    public ResultadoParseo leer(Path ruta) throws IOException {
        List<Alquiler> alquileres = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        int leidas = 0;
        int descartadas = 0;

        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String encabezado = lector.readLine();
            // El encabezado decide el ancho que TODAS las filas deben tener.
            int anchoEsperado;
            if (ENCABEZADO_INICIAL.equals(encabezado)) {
                anchoEsperado = 8;
            } else if (ENCABEZADO_EVOLUCIONADO.equals(encabezado)) {
                anchoEsperado = 10;
            } else {
                throw new IllegalArgumentException("encabezado incorrecto");
            }

            String linea;
            while ((linea = lector.readLine()) != null) {
                leidas++;
                try {
                    String[] campos = Arrays.stream(linea.split(",", -1))
                            .map(String::strip)
                            .toArray(String[]::new);
                    // 1) ancho  2) cancelado  3) todo lo demas lo valida el dominio
                    if (campos.length != anchoEsperado) {
                        throw new IllegalArgumentException("cantidad de columnas incorrecta");
                    }
                    if (campos[7].equals("CANCELADO")) {
                        descartadas++;
                        continue;
                    }
                    alquileres.add(Alquiler.desdeCampos(campos));
                } catch (IllegalArgumentException error) {
                    errores.add("Linea " + (leidas + 1) + ": " + error.getMessage());
                }
            }
        }
        return new ResultadoParseo(alquileres, leidas, descartadas, errores);
    }
}
