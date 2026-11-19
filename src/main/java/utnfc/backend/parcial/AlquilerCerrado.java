package utnfc.backend.parcial;

import static java.lang.Math.max;

public class AlquilerCerrado extends Alquiler {
    private final int kmFinal;
    private final int diasReales;

    public AlquilerCerrado(String id, String cliente, String categoria, String sucursal,
                           int dias, double tarifaDiaria, int kmIncluidos,
                           int kmFinal, int diasReales) {
        super(id, cliente, categoria, sucursal, dias, tarifaDiaria, kmIncluidos);
        if (kmFinal < 0 || diasReales < 0) {
            throw new IllegalArgumentException("kmFinal y diasReales no pueden ser negativos");
        }
        if (diasReales < dias) {
            throw new IllegalArgumentException("diasReales no puede ser menor que dias");
        }
        this.kmFinal = kmFinal;
        this.diasReales = diasReales;
    }

    @Override
    public double importe() {
        return diasReales * getTarifaDiaria()
                + 12 * max(0, kmFinal - getKmIncluidos())
                + 500 * max(0, diasReales - getDias());
    }

    @Override
    public boolean estaCerrado() {
        return true;
    }

    @Override
    public String estado() {
        return "CERRADO";
    }

    public int getKmFinal() {
        return kmFinal;
    }

    public int getDiasReales() {
        return diasReales;
    }
}
