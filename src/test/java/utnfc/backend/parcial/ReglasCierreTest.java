package utnfc.backend.parcial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ReglasCierreTest {
    private static final String H8 = "id,cliente,categoria,sucursal,dias,tarifaDiaria,kmIncluidos,estado\n";
    private static final String H10 = "id,cliente,categoria,sucursal,dias,tarifaDiaria,kmIncluidos,estado,kmFinal,diasReales\n";

    private ResultadoParseo leer(String contenido) throws Exception {
        Path p = Files.createTempFile("alq", ".csv");
        Files.writeString(p, contenido);
        return new ParserAlquileres().leer(p);
    }

    @Test
    void ejemploDeLaConsigna() {
        Alquiler a = Alquiler.desdeCampos(new String[] {
                "X", "A", "SEDAN", "Centro", "3", "5000", "400", "CERRADO", "460", "4" });
        assertEquals(21220, a.importe());
    }

    @Test
    void abiertoDelFormatoNuevoConCamposVaciosEsValido() throws Exception {
        ResultadoParseo r = leer(H10 + "A,Ana,SEDAN,Centro,2,10,100,ABIERTO,,\n");
        assertEquals(1, r.getProcesadas());
        assertEquals("ABIERTO", r.getAlquileres().getFirst().estado());
    }

    @Test
    void abiertoConDatosDeCierreEsInvalido() throws Exception {
        assertEquals(1, leer(H10 + "A,Ana,SEDAN,Centro,2,10,100,ABIERTO,150,\n").getInvalidas());
        assertEquals(1, leer(H10 + "A,Ana,SEDAN,Centro,2,10,100,ABIERTO,,3\n").getInvalidas());
    }

    @Test
    void cerradoInvalido() throws Exception {
        // diasReales < dias, kmFinal negativo, vacios, no numericos
        ResultadoParseo r = leer(H10
                + "A,Ana,SEDAN,Centro,3,10,100,CERRADO,150,2\n"
                + "B,Ana,SEDAN,Centro,3,10,100,CERRADO,-1,3\n"
                + "C,Ana,SEDAN,Centro,3,10,100,CERRADO,,\n"
                + "D,Ana,SEDAN,Centro,3,10,100,CERRADO,x,3\n");
        assertEquals(4, r.getInvalidas());
        assertEquals(0, r.getProcesadas());
    }

    @Test
    void elAnchoDebeCoincidirConElEncabezado() throws Exception {
        // 10 columnas bajo encabezado de 8, y 8 columnas bajo encabezado de 10
        assertEquals(1, leer(H8 + "A,Ana,SEDAN,Centro,2,10,100,ABIERTO,,\n").getInvalidas());
        assertEquals(1, leer(H10 + "A,Ana,SEDAN,Centro,2,10,100,ABIERTO\n").getInvalidas());
    }

    @Test
    void cerradoEnFormatoInicialEsInvalido() throws Exception {
        assertEquals(1, leer(H8 + "A,Ana,SEDAN,Centro,2,10,100,CERRADO\n").getInvalidas());
    }

    @Test
    void canceladoConNumerosImposiblesSeDescarta() throws Exception {
        ResultadoParseo r = leer(H10 + "A,,SEDAN,Centro,-5,x,-1,CANCELADO,-3,abc\n");
        assertEquals(1, r.getDescartadas());
        assertEquals(0, r.getInvalidas());
    }

    @Test
    void estadoDesconocidoEsInvalido() throws Exception {
        assertEquals(1, leer(H10 + "A,Ana,SEDAN,Centro,2,10,100,CERADO,150,3\n").getInvalidas());
    }

    @Test
    void conteoVacio() {
        assertTrue(new Garage(java.util.List.of()).conteoPorEstado().isEmpty());
    }
}
