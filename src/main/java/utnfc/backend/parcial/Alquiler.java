package utnfc.backend.parcial;

public class Alquiler {
    private final String id;
    private final String cliente;
    private final String categoria;
    private final String sucursal;
    private final int dias;
    private final double tarifaDiaria;
    private final int kmIncluidos;
    private int kmFinal;
    private int diasReales;

    public Alquiler(String id, String cliente, String categoria, String sucursal,
                    int dias, double tarifaDiaria, int kmIncluidos) {
        this.id = textoObligatorio(id, "id");
        this.cliente = textoObligatorio(cliente, "cliente");
        this.categoria = textoObligatorio(categoria, "categoria");
        this.sucursal = textoObligatorio(sucursal, "sucursal");
        if (dias <= 0 || tarifaDiaria <= 0 || kmIncluidos < 0) {
            throw new IllegalArgumentException("dias, tarifa y kilometros deben ser validos");
        }
        this.dias = dias;
        this.tarifaDiaria = tarifaDiaria;
        this.kmIncluidos = kmIncluidos;
    }
    public Alquiler(String id, String cliente, String categoria, String sucursal,
                    int dias, double tarifaDiaria, int kmIncluidos, int kmFinal, int diasReales)
    {
        this(id, cliente, categoria, sucursal, dias, tarifaDiaria, kmIncluidos);
        if(kmFinal < 0 || diasReales < 0)
        {
            throw new IllegalArgumentException("km finales y dias reales deben ser validos");
        }
        this.kmFinal = kmFinal;
        this.diasReales = diasReales;
    }


    public static Alquiler desdeCampos(String[] campos) {
        if (campos.length != 8 && campos.length != 10) {
            throw new IllegalArgumentException("cantidad de columnas incorrecta");
        }
        if (!campos[7].equals("ABIERTO") && !campos[7].equals("CERRADO")) {
            throw new IllegalArgumentException("el modelo del parcial solo admite ABIERTO o CERRADO");
        }
        if (campos.length ==8)
        {
            return new Alquiler(campos[0], campos[1], campos[2], campos[3],
                    Integer.parseInt(campos[4]), Double.parseDouble(campos[5]),
                    Integer.parseInt(campos[6]));
        }
        else
        {
            int campoKm = Integer.parseInt(campos[8]);
            int campoDias= Integer.parseInt(campos[9]);
            if (campoKm < 0 || campoDias < 0)
            { throw new IllegalArgumentException("Cantidad de km reales o cantidad de dias invalido");}
            return new AlquilerCerrado(campos[0], campos[1], campos[2], campos[3],
                    Integer.parseInt(campos[4]), Double.parseDouble(campos[5]),
                    Integer.parseInt(campos[6]), campoKm, campoDias);
        }
    }

    public double importe() {
        return dias * tarifaDiaria;
    }

    public boolean estaCerrado() {
        return false;
    }

    public String estado() {
        return "ABIERTO";
    }

    public String getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getSucursal() {
        return sucursal;
    }

    public int getDias() {
        return dias;
    }

    public int getDiasReales() {return diasReales;}
    public int getKmFinal(){return kmFinal;}
    public double getTarifaDiaria() {
        return tarifaDiaria;
    }

    public int getKmIncluidos() {
        return kmIncluidos;
    }

    protected static String textoObligatorio(String valor, String nombre) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nombre + " no puede estar vacio");
        }
        return valor.strip();
    }
}
