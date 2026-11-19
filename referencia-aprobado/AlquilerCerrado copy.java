package utnfc.backend.parcial;

import static java.lang.Math.max;

public class AlquilerCerrado extends Alquiler {
    private final int kmFinal;
    private final int diasReales;

    public AlquilerCerrado(
        String id,
        String cliente,
        String categoria,
        String sucursal,
        int dias,
        double tarifaDiaria,
        int kmIncluidos,
        int kmFinal,
        int diasReales
    ) {
        super(
            id,
            cliente,
            categoria,
            sucursal,
            dias,
            tarifaDiaria,
            kmIncluidos
        );
        if (diasReales <= 0 || kmFinal < 0) {
            throw new IllegalArgumentException("dias reales y kilometros finales deben ser validos");
        }
        this.diasReales = diasReales;
        this.kmFinal = kmFinal;
    }

    @Override
    public double importe() {
        return (diasReales * getTarifaDiaria())
                + (12 * max(0, kmFinal - getKmIncluidos()))
                + (500 * max(0, diasReales - getDias()));
    }
}
