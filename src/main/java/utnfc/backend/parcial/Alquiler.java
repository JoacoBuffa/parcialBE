package utnfc.backend.parcial;

public class Alquiler {
    private final String id;
    private final String cliente;
    private final String categoria;
    private final String sucursal;
    private final int dias;
    private final double tarifaDiaria;
    private final int kmIncluidos;

    // Las invariantes comunes se validan UNA sola vez, aca.
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

    /**
     * Unico punto de conversion texto -> objeto. Elige la subclase segun el estado.
     * No decide nada sobre el ancho esperado por el encabezado (eso es del parser).
     */
    public static Alquiler desdeCampos(String[] campos) {
        if (campos.length != 8 && campos.length != 10) {
            throw new IllegalArgumentException("cantidad de columnas incorrecta");
        }
        String estado = campos[7];
        switch (estado) {
            case "ABIERTO":
                if (campos.length == 10 && (!campos[8].isEmpty() || !campos[9].isEmpty())) {
                    throw new IllegalArgumentException(
                            "un alquiler abierto no puede tener datos de cierre");
                }
                return new Alquiler(campos[0], campos[1], campos[2], campos[3],
                        Integer.parseInt(campos[4]), Double.parseDouble(campos[5]),
                        Integer.parseInt(campos[6]));
            case "CERRADO":
                if (campos.length != 10) {
                    throw new IllegalArgumentException(
                            "un alquiler cerrado requiere kmFinal y diasReales");
                }
                return new AlquilerCerrado(campos[0], campos[1], campos[2], campos[3],
                        Integer.parseInt(campos[4]), Double.parseDouble(campos[5]),
                        Integer.parseInt(campos[6]),
                        Integer.parseInt(campos[8]), Integer.parseInt(campos[9]));
            default:
                throw new IllegalArgumentException("estado desconocido: " + estado);
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
