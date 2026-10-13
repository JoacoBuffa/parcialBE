# Tests nuevos del parcial

Esta carpeta contiene dos clases de test para incorporar al proyecto preparcial:

- `EvolucionTest.java`: verifica la creación polimórfica de un alquiler cerrado
  (`AlquilerCerrado`), el cálculo de su importe y el conteo de alquileres por estado.
- `ParserAlquileresTest.java`: **reemplaza** al test homónimo del proyecto inicial. Conserva
  los casos originales (procesamiento y descarte, precedencia del ancho ante `CANCELADO`) y
  agrega un caso de compatibilidad entre el formato inicial (8 columnas) y el evolucionado (10
  columnas).

## Cómo incorporarlos

Desde la raíz de su copia de `01-modelo-inicial`:

```sh
cp -R tests-nuevos/utnfc src/test/java/
```

Esto fusiona el paquete `utnfc.backend.parcial` y sobrescribe el `ParserAlquileresTest.java`
existente con la versión ampliada. Es el comportamiento esperado.

Antes de resolver la evolución, algunos de estos tests deben fallar: eso demuestra que cubren
funcionalidad todavía ausente en el modelo inicial, no un error de compilación arbitrario.
