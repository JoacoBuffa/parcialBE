package utnfc.backend.parcial;

public class Alquiler {
    private final String id;
    private final String cliente;
    private final String categoria;
    private final String sucursal;
    private final int dias;
    private final double tarifaDiaria;
    private final int kmIncluidos;

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

    public static Alquiler desdeCampos(String[] campos) {
        if (campos.length != 8 && campos.length != 10) {
            throw new IllegalArgumentException("cantidad de columnas incorrecta");
        }
        if (campos[7].equals("ABIERTO")) {
            return new Alquiler(campos[0], campos[1], campos[2], campos[3],
                    Integer.parseInt(campos[4]), Double.parseDouble(campos[5]),
                    Integer.parseInt(campos[6]));
        }
        if (campos[7].equals("CERRADO")) {
            return new AlquilerCerrado(campos[0], campos[1], campos[2], campos[3],
                    Integer.parseInt(campos[4]), Double.parseDouble(campos[5]),
                    Integer.parseInt(campos[6]), Integer.parseInt(campos[8]),
                    Integer.parseInt(campos[9]));
        }

        throw new IllegalArgumentException("el modelo solo admite ABIERTO y CERRADO");
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
