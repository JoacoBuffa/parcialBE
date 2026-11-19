# Tutorial: cómo resolver la evolución del parcial (paso a paso)

Sirve para el recuperatorio: la estructura es siempre la misma. Hay un modelo inicial
(`Alquiler`, `ParserAlquileres`, `ResultadoParseo`, `Garage`, `Main`) y la consigna agrega un
**subtipo** (`CERRADO`) con **columnas nuevas**. El código final de referencia está en `src/main`.

## 0. La idea central (lo que separa aprobar de "hacerla compleja")

> **Cada regla se valida en UN solo lugar, y ese lugar es el que mejor la conoce.**

| Regla                                                                  | Dónde vive                                                |
| ---------------------------------------------------------------------- | --------------------------------------------------------- |
| Ancho de fila según encabezado, descartar `CANCELADO`                  | `ParserAlquileres` (es del _archivo_)                     |
| Elegir la subclase según el estado, reglas de ABIERTO/CERRADO, números | `Alquiler.desdeCampos` + constructores (es del _dominio_) |
| Cálculo del importe, nombre del estado                                 | método polimórfico en cada clase                          |

Tu versión anterior repetía validaciones en el parser **y** en el constructor, y metía
`kmFinal`/`diasReales` dentro de `Alquiler` (con `if`s para saber si aplicaban). Eso es
justamente lo que el polimorfismo evita.

## 1. Leer la consigna con lápiz: sacar la tabla de reglas

Antes de codear, armar esta tabla (es lo que el docente evalúa):

| Caso                                                     | Resultado                            |
| -------------------------------------------------------- | ------------------------------------ |
| Encabezado ni de 8 ni de 10 columnas exactas             | `IllegalArgumentException` (aborta)  |
| Ancho de la fila ≠ ancho del **encabezado**              | inválida                             |
| `CANCELADO` (ancho ok)                                   | descartada, **sin validar nada más** |
| Estado desconocido                                       | inválida                             |
| `ABIERTO` con `kmFinal` o `diasReales` no vacíos         | inválida                             |
| `CERRADO` en formato de 8 columnas                       | inválida                             |
| `CERRADO`: `kmFinal`/`diasReales` no enteros o negativos | inválida                             |
| `CERRADO`: `diasReales < dias`                           | inválida                             |

Y el **orden** (la consigna lo remarca): **1) ancho → 2) cancelado → 3) estado, números y reglas**.

Fórmula del cerrado:
`diasReales*tarifa + 12*max(0, kmFinal-kmIncluidos) + 500*max(0, diasReales-dias)`.
Verificá con el ejemplo del PDF: `4*5000 + 60*12 + 1*500 = 21220`.

## 2. Paso a paso

### Paso 1 – Correr los tests nuevos y ver cuáles fallan

```sh
cp -R tests-nuevos/utnfc src/test/java/
mvn test
```

Fallan los que usan `AlquilerCerrado`/`conteoPorEstado` porque todavía no existen. Es lo esperado.

### Paso 2 – `AlquilerCerrado extends Alquiler` (la clase tipificada)

Solo lo que es _propio_ del cerrado:

```java
public class AlquilerCerrado extends Alquiler {
    private final int kmFinal;
    private final int diasReales;

    public AlquilerCerrado(String id, ..., int kmIncluidos, int kmFinal, int diasReales) {
        super(id, ..., kmIncluidos);          // las invariantes comunes ya las valida Alquiler
        if (kmFinal < 0 || diasReales < 0) throw new IllegalArgumentException(...);
        if (diasReales < dias)             throw new IllegalArgumentException(...);
        this.kmFinal = kmFinal; this.diasReales = diasReales;
    }

    @Override public double importe() {
        return diasReales * getTarifaDiaria()
             + 12  * max(0, kmFinal - getKmIncluidos())
             + 500 * max(0, diasReales - getDias());
    }
    @Override public String  estado()      { return "CERRADO"; }
    @Override public boolean estaCerrado() { return true; }
}
```

Detalles que se pierden puntos: `dias` se usa en el constructor como parámetro (no hace falta
getter) y el `if` de `diasReales < dias` va **acá**, no en el parser.

### Paso 3 – `Alquiler` NO se toca salvo `desdeCampos`

Se borran los campos `kmFinal`/`diasReales`, el segundo constructor y sus getters: un abierto no
tiene datos de cierre. `desdeCampos` pasa a ser `static` y decide la subclase con un `switch`
sobre el estado:

```java
switch (campos[7]) {
    case "ABIERTO" -> {
        if (campos.length == 10 && (!campos[8].isEmpty() || !campos[9].isEmpty()))
            throw new IllegalArgumentException("un alquiler abierto no puede tener datos de cierre");
        return new Alquiler(... 7 campos ...);
    }
    case "CERRADO" -> {
        if (campos.length != 10) throw new IllegalArgumentException("cerrado requiere kmFinal y diasReales");
        return new AlquilerCerrado(... 9 campos ...);
    }
    default -> throw new IllegalArgumentException("estado desconocido");
}
```

Sirve para ambos formatos con un único método (por eso conviene `static`: el `EvolucionTest`
llama `Alquiler.desdeCampos(...)` y recibe un `AlquilerCerrado` sin que nadie escriba `new`).
`Integer.parseInt` lanza `NumberFormatException`, que **es** una `IllegalArgumentException`, así
que el parser la atrapa sola: no hace falta `try/catch` extra ni validar "si es número".

### Paso 4 – `ParserAlquileres`: aceptar los dos encabezados sin duplicar el proceso

Un solo bucle. El encabezado define el ancho esperado de **todas** las filas:

```java
int anchoEsperado;
if (ENCABEZADO_INICIAL.equals(encabezado))            anchoEsperado = 8;
else if (ENCABEZADO_EVOLUCIONADO.equals(encabezado))  anchoEsperado = 10;
else throw new IllegalArgumentException("encabezado incorrecto");
...
if (campos.length != anchoEsperado) throw new IllegalArgumentException("cantidad de columnas incorrecta");
if (campos[7].equals("CANCELADO")) { descartadas++; continue; }
alquileres.add(Alquiler.desdeCampos(campos));   // todo lo demás lo valida el dominio
```

Por qué **no** alcanza con `length != 8 && length != 10`: la consigna dice que cada fila debe
tener _exactamente el ancho de su encabezado_. Una fila de 10 columnas bajo el encabezado de 8
es inválida (test `validaAnchoAntesDeDescartarCancelado`).

### Paso 5 – `ResultadoParseo`: no tocarlo

Tenías una lista extra `alquilerCerrados` que nadie necesita: `getAlquileres()` ya devuelve
abiertos y cerrados mezclados (polimorfismo). Quitarla simplifica el constructor.

### Paso 6 – `Garage.conteoPorEstado()` sin `instanceof`

```java
return alquileres.stream().collect(groupingBy(Alquiler::estado, counting()));
```

Con colección vacía devuelve un mapa vacío (lo que pide la consigna). `total()` y
`totalesPorSucursal()` no cambian: llaman a `importe()` y Java elige la versión correcta.

### Paso 7 – `Main`

Ya usa `datos/datos-parcial.csv` por defecto; solo agregar el conteo por estado:

```java
System.out.printf("Conteo por estado: %s%n", garage.conteoPorEstado());
```

Sin lógica de negocio en `Main`, solo imprimir.

## 3. Errores concretos que había en las dos versiones

**Tu versión**

- `campos[7].equals("ABIERTO") && campos[8].isEmpty() || campos[9].isEmpty()`: precedencia de
  `&&`/`||` mal y la condición está **invertida** (rechazaba abiertos válidos). Además, con 8
  columnas `campos[8]` tira `ArrayIndexOutOfBoundsException`, que el `catch` no atrapa y
  **aborta toda la carga**.
- Validaba `diasReales >= dias` **antes** de saber si la fila era CANCELADO/ABIERTO → rompe la
  precedencia de la consigna.
- No exigía que el ancho coincida con el encabezado.
- Validaciones duplicadas (parser + `desdeCampos` + constructor), campos de cierre dentro de
  `Alquiler`, un `System.out.println` de debug, lista `alquilerCerrados` sin uso.
- `Garage.totalesAlquileresAbiertos()` contaba _todos_ (nombre engañoso).

**Versión del amigo** (bien el polimorfismo, pero)

- Typo `"CERADO"` en el parser: **todo CERRADO queda inválido** por "estado desconocido".
- No valida `diasReales >= dias`, ni que un ABIERTO no traiga datos de cierre, ni el ancho según
  encabezado, ni que CERRADO exija 10 columnas.
- `conteoPorEstado` con `instanceof`: funciona, pero es lo que el polimorfismo (`estado()`)
  reemplaza.

## 4. Checklist para el recuperatorio

1. ¿Armé la tabla de reglas y el orden (ancho → cancelado → resto)?
2. ¿Cada validación está en un solo lugar? (parser: archivo; constructor: dominio)
3. ¿La subclase solo tiene lo propio y sobreescribe `importe()`/`estado()`?
4. ¿Nada de `instanceof` ni `if (estado == ...)` en `Garage`/`Main`?
5. ¿Probé: ABIERTO con datos de cierre, CERRADO con 8 columnas, `diasReales < dias`,
   CANCELADO con basura, ancho ≠ encabezado, estado con typo?
6. `mvn test`, `java -cp target/classes utnfc.backend.parcial.Main`, `DECISIONES.md`, ZIP sin
   `target/` ni `.git/`.
