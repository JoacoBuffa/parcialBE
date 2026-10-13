package utnfc.backend.parcial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ParserAlquileresTest {
    @Test
    void procesaYDescarta() throws Exception {
        Path path = Files.createTempFile("alq", "csv");
        String content = "id,cliente,categoria,sucursal,dias,tarifaDiaria,"
                + "kmIncluidos,estado\n"
                + "A,Ana,SEDAN,Centro,2,10,100,ABIERTO\n"
                + "B,Beto,SUV,Norte,1,20,100,CANCELADO\n"
                + "C,Caro,SUV,Sur,x,20,100,ABIERTO\n";
        Files.writeString(path, content);

        ResultadoParseo result = new ParserAlquileres().leer(path);

        assertEquals(3, result.getLeidas());
        assertEquals(1, result.getAlquileres().size());
        assertEquals(1, result.getDescartadas());
        assertEquals(1, result.getInvalidas());
    }

    @Test
    void aceptaFormatoInicialYFormatoEvolucionado() throws Exception {
        Path inicial = Files.createTempFile("alq-inicial", "csv");
        Files.writeString(inicial,
                "id,cliente,categoria,sucursal,dias,tarifaDiaria,kmIncluidos,estado\n"
                        + "A,Ana,SEDAN,Centro,2,10,100,ABIERTO\n");
        Path evolucionado = Files.createTempFile("alq-evolucionado", "csv");
        Files.writeString(evolucionado,
                "id,cliente,categoria,sucursal,dias,tarifaDiaria,kmIncluidos,estado,kmFinal,diasReales\n"
                        + "C,Caro,SUV,Sur,2,100,100,CERRADO,150,3\n");

        ResultadoParseo inicialResult = new ParserAlquileres().leer(inicial);
        ResultadoParseo evolucionadoResult = new ParserAlquileres().leer(evolucionado);

        assertEquals(1, inicialResult.getAlquileres().size());
        assertInstanceOf(AlquilerCerrado.class, evolucionadoResult.getAlquileres().getFirst());
        assertEquals(1_400, evolucionadoResult.getAlquileres().getFirst().importe());
    }

    @Test
    void validaAnchoAntesDeDescartarCancelado() throws Exception {
        Path path = Files.createTempFile("alq-ancho", "csv");
        Files.writeString(path,
                "id,cliente,categoria,sucursal,dias,tarifaDiaria,kmIncluidos,estado\n"
                        + "B,Beto,SUV,Norte,1,20,100,CANCELADO,extra\n");

        ResultadoParseo result = new ParserAlquileres().leer(path);

        assertEquals(0, result.getDescartadas());
        assertEquals(1, result.getInvalidas());
    }
}
