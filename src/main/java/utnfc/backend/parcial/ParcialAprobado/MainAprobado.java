package utnfc.backend.parcial.ParcialAprobado ;

import java.nio.file.Path;

import utnfc.backend.parcial.ParserAlquileres;

/** Ejecutable mínimo para inspeccionar el procesamiento del modelo inicial. */
public class MainAprobado {
    public static void main(String[] args) throws Exception {
        Path ruta = Path.of(args.length == 0 ? "datos/datos-parcial.csv" : args[0]);
        ResultadoParseoAprobado resultado = new ParserAlquileresAprobado().leer(ruta);
        GarageAprobado garage = new GarageAprobado (resultado.getAlquileres());
        System.out.printf("Archivo: %s%nFilas: %d | procesadas: %d | descartadas: %d | inválidas: %d%n",
                ruta, resultado.getLeidas(), resultado.getAlquileres().size(),
                resultado.getDescartadas(), resultado.getInvalidas());
        System.out.printf("Total: %.2f%nTotales por sucursal: %s%n", garage.total(), garage.totalesPorSucursal());
        if (!resultado.getErrores().isEmpty()) {
            System.out.println("Errores: " + resultado.getErrores());
        }
    }
}
