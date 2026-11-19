package utnfc.backend.parcial.ParcialAprobado;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GarageAprobado {
    private final List<Alquiler> alquileres;
    public GarageAprobado(List<Alquiler> alquileres) {
        this.alquileres = List.copyOf(alquileres);
    }

    public double total() {
        return alquileres.stream().mapToDouble(Alquiler::importe).sum();
    }

    public List<Alquiler> filtrar(Predicate<Alquiler> criterio) {
        return alquileres.stream().filter(criterio).toList();
    }

    public Map<String, Double> totalesPorSucursal() {
        return alquileres.stream().collect(Collectors.groupingBy(
                Alquiler::getSucursal,
                Collectors.summingDouble(Alquiler::importe)));
    }

    public List<Alquiler> todos() {
        return alquileres;
    }

    public Map<String, Long> conteoPorEstado() {
        return alquileres.stream()
                .collect(Collectors.groupingBy(
                a -> a instanceof AlquilerCerrado ? "CERRADO" : "ABIERTO",
                Collectors.counting())
                );
    }
}
