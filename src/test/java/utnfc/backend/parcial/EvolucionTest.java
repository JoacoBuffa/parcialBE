package utnfc.backend.parcial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;

import org.junit.jupiter.api.Test;

class EvolucionTest {
    @Test
    void creaUnAlquilerCerradoPolimorfico() {
        Alquiler alquiler = Alquiler.desdeCampos(new String[] {
                "X", "Ana", "SEDAN", "Centro", "2", "100", "100",
                "CERRADO", "150", "3"
        });

        assertInstanceOf(AlquilerCerrado.class, alquiler);
        assertEquals(1_400, alquiler.importe());
    }

    @Test
    void cuentaEstadosSinConocerClasesConcretas() {
        Garage garage = new Garage(List.of(
                Alquiler.desdeCampos(new String[] {
                        "X", "A", "SEDAN", "Centro", "1", "10", "10", "ABIERTO"
                }),
                Alquiler.desdeCampos(new String[] {
                        "Y", "B", "SEDAN", "Centro", "1", "10", "10",
                        "CERRADO", "10", "1"
                })));

        assertEquals(1, garage.conteoPorEstado().get("CERRADO"));
    }
}
