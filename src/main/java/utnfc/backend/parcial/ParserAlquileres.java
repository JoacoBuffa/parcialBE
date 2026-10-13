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
    public ResultadoParseo leer(Path ruta) throws IOException {
        List<Alquiler> alquileres = new ArrayList<>();
        List<AlquilerCerrado> alquilerCerrados = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        int leidas = 0;
        int descartadas = 0;
        String encabezado ="id,cliente,categoria,sucursal,dias,tarifaDiaria,kmIncluidos,estado";
        String encabezadoNuevo = encabezado + ",kmFinal,diasReales";

        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String encabezadoLeido = lector.readLine();
            System.out.println((encabezadoNuevo));
            if (!encabezado.equals(encabezadoLeido) && (!encabezadoNuevo.equals(encabezadoLeido))) {
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
                    if (!campos[7].equals("ABIERTO") && !campos[7].equals("CERRADO")) {
                        throw new IllegalArgumentException("estado desconocido");
                    }
                    //Si una fila abierto trae valores del nuevo formato da invalido

                    if (campos[7].equals("ABIERTO") && campos[8].isEmpty()|| campos[9].isEmpty())
                    {
                        throw new IllegalArgumentException("un alquiler abierto no puede tener km reales ni dias reales");
                    }
                    //Dias reales debe ser mayor o igual a dias
                    int campoDia = Integer.parseInt(campos[4]);
                    int campoDiasReales = Integer.parseInt(campos[9]);
                    if(campoDiasReales < campoDia)
                    {
                        throw new IllegalArgumentException("Dias reales invalidos");
                    }

                    if (campos.length == 10)
                    {
                        if(campos[7].equals("CERRADO"))
                        {
                            int campoKm = Integer.parseInt(campos[8]);
                            int campoDias= Integer.parseInt(campos[9]);
                            if (campoKm < 0 || campoDias < 0)
                            { throw new IllegalArgumentException("Cantidad de km reales o cantidad de dias invalido");}
                            alquileres.add(Alquiler.desdeCampos(campos));
                            continue;
                        }
                    }

                    alquileres.add(Alquiler.desdeCampos(campos));
                } catch (IllegalArgumentException error) {
                    errores.add("Linea " + (leidas + 1) + ": " + error.getMessage());
                }
            }
        }
        return new ResultadoParseo(alquileres, alquilerCerrados, leidas, descartadas, errores);
    }
}
