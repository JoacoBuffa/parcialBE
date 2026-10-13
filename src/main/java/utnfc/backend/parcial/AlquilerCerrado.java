package utnfc.backend.parcial;

public class AlquilerCerrado extends Alquiler {
    public AlquilerCerrado (String id, String cliente, String categoria, String sucursal,
                            int dias, double tarifaDiaria, int kmIncluidos)

    {
        super(id, cliente, categoria, sucursal, dias, tarifaDiaria, kmIncluidos);
    }

    public AlquilerCerrado (String id, String cliente, String categoria, String sucursal,
                            int dias, double tarifaDiaria, int kmIncluidos, int kmFinal, int diasReales)

    {

        super(id, cliente, categoria, sucursal, dias, tarifaDiaria, kmIncluidos, kmFinal, diasReales);
    }



    @Override
    public double importe()
    {   double plusKmExcedente;
        double plusDiasExcedente;

        double usoReal = getDiasReales() * getTarifaDiaria();

        int kmExcedente = getKmFinal() - getKmIncluidos();

        int diasExcedente = getDiasReales() - getDias();

        if (kmExcedente > 0)
        {
            plusKmExcedente = 12 * kmExcedente;
        }
        else {plusKmExcedente = 0;}

        if (diasExcedente > 0)
        {
            plusDiasExcedente = 500 * diasExcedente;
        }
        else {plusDiasExcedente = 0;}
        return usoReal + plusKmExcedente + plusDiasExcedente;

    }

}
