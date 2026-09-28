# Paso 4 · Límite del Agregado — Subdominio Alertas e Incidentes

**Dev 3** · Bounded Context: Alertas e Incidentes · Raíz del Agregado: `Alerta`

## 1. `Alerta` es la raíz

`Alerta` (`co.edu.uptc.cadena_de_frio_g6.alertas.dominio.Alerta`) es la única raíz de Agregado de este subdominio. No hay otras entidades dentro del agregado en esta entrega — `SeveridadAlerta` es un Value Object, no una entidad con identidad propia, así que vive **dentro** del agregado sin ser una raíz aparte.

Todo acceso o modificación de una `Alerta` pasa por su propia API pública (`resolver()`) o por los servicios de dominio del mismo paquete (`EscalamientoAlertaService.evaluar(...)`, que llama al mutador `marcarComoCritica()`, de visibilidad de paquete). Nada fuera de `co.edu.uptc.cadena_de_frio_g6.alertas` puede cambiar la severidad de una alerta directamente.

## 2. Referencia a otros agregados solo por id

`Alerta` necesita saber **qué envío** y **qué sensor** la originaron, pero **nunca** carga esos agregados completos:

```java
public class Alerta {
    private final UUID id;
    private final UUID envioId;   // no Envio envio
    private final UUID sensorId;  // no Sensor sensor
    ...
}
```

Esto es deliberado, no una limitación de la entrega:

- **Consistencia transaccional.** El único invariante que `Alerta` debe garantizar en una misma transacción es el suyo propio (severidad, resuelta/sin resolver). No le corresponde validar el rango de temperatura del envío ni el estado del sensor — eso es responsabilidad de sus propios agregados, en sus propias transacciones.
- **Acoplamiento entre Bounded Contexts.** Si `Alerta` guardara una referencia a `Envio` o a `Sensor` completos, cualquier cambio en esos agregados (nuevos campos, cambios de invariante) obligaría a tocar el modelo de Alertas. Con solo el id, el acoplamiento se reduce a "existe un envío/sensor con este identificador", que es estable.
- **Coherente con la arquitectura objetivo (ver [`Docs/arquitectura/diagrama-c4.md`](../../Docs/arquitectura/diagrama-c4.md)).** En el diseño a microservicios, `Alerta` va a vivir en su propio servicio con su propia base de datos (`BD Alertas`). Un `envioId`/`sensorId` como UUID es justamente lo que se puede persistir y pasar entre servicios sin acoplar esquemas.

## 3. Cómo entra la información de otros contextos

`Alerta` nunca pregunta directamente por lecturas ni por condiciones requeridas. Toda esa información entra como **datos primitivos** que otro componente (fuera de este agregado) ya calculó:

- `EscalamientoAlertaService.evaluar(Alerta alerta, int lecturasFueraDeRangoConsecutivas)` recibe el conteo de lecturas fuera de rango ya calculado — no una lista de `Lectura` del subdominio de Sensores.
- La comparación de una lectura contra el rango de temperatura del envío (`DeteccionFueraDeRangoService`, evento `LecturaFueraDeRangoDetectada`) es responsabilidad de otro Bounded Context (Lecturas de Sensores), no de `Alerta`.

## 4. Qué falta fuera de este agregado (pendiente, no bloqueante para esta entrega)

- ~~Un puerto/interfaz para que `CierreEnvioService` (Dev 1) pregunte "¿hay alertas críticas sin resolver para este `envioId`?"~~ — implementado como `ConsultaAlertasCriticasUseCase` (`alertas.aplicacion`), servido por `AlertaService` y respaldado por el puerto secundario `AlertaRepository` (adaptador JPA en `alertas.infraestructura`). Sigue pendiente exponerlo por REST para que Dev 1 lo consuma sin JDBC cruzado, coherente con `Docs/arquitectura/diagrama-c4.md` ("Corrección de diseño #2").
- La confirmación de si `Incidente` pertenece a este agregado o al de Envíos (ver `evidencias/dev-3/glosario.md`, §5) — por ahora `Alerta` no lo referencia de ninguna forma.
