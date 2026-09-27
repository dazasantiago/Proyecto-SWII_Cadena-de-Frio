# Paso 4 · Límite del Agregado — Subdominio Envíos y condiciones

Sin cambios de código: la regla ya se cumplía desde el paso 3, cuando se creó `Envio`. Este paso la deja documentada explícitamente, como pide el taller.

## Raíz del Agregado

**`Envio`** es la raíz. Es la única clase de `envios/` con identidad propia y ciclo de vida (`@Entity` cuando el proyecto sume persistencia). Todo acceso al Agregado pasa por ella.

## Qué vive dentro del Agregado

| Elemento | Por qué está dentro |
|---|---|
| `EnvioId id` | Es la identidad del propio Agregado. |
| `EstadoEnvio estado` | El ciclo de vida (`REGISTRADO` → `EN_TRANSITO` → `CERRADO`) es responsabilidad exclusiva de `Envio`; nadie más lo cambia directamente. |

Según el glosario ([glosario.md](glosario.md)), `CondicionRequerida` (con sus `RangoTemperatura` / `RangoHumedad`) y el resumen de cumplimiento también pertenecen al Agregado. Todavía no están en `Envio.java` porque el taller los introduce en el paso 5, a través de la Factory.

## Por qué otras entidades NO están dentro

- **`Alerta`** (contexto Alertas e Incidentes): un envío puede tener varias alertas a lo largo del transporte. Meterlas dentro de `Envio` rompería la regla de Agregados pequeños — cada alerta cambia con su propio ritmo, sin relación con el resto del envío.
- **`Lectura` / `Sensor`** (contexto Lecturas de Sensores): son de altísimo volumen (una cada pocos segundos). Vivir dentro de `Envio` haría el Agregado enorme y lento de cargar, además de mezclar dos Lenguajes Ubicuos distintos.
- **`Incidente`**: por ahora se trata igual que las alertas — pertenece al ciclo del envío, pero se reporta de forma independiente y puede llegar en cualquier momento del transporte. Queda pendiente confirmar si termina como parte de este Agregado o como uno propio (ver glosario, sección 5).

## Cómo se referencian entidades de otros contextos: solo por id

La consecuencia visible en el código, equivalente a `Publicacion.investigadorCorreo` en RICA:

- **`EnvioId`** es un `record` que envuelve un `String`. Es lo único que otros contextos reciben para identificar un envío — nunca un objeto `Envio` completo.
- **`ConsultaAlertasCriticas.tieneAlertasCriticasSinResolver(EnvioId envioId)`** — la interfaz que `CierreEnvioService` usa para preguntarle a Alertas. Recibe un `EnvioId`, nunca un `Envio`; devuelve un `boolean`, nunca una lista de `Alerta`.
- **`CierreEnvioService.cerrar(Envio envio)`** solo conoce su propio Agregado. Llama a `consultaAlertasCriticas.tieneAlertasCriticasSinResolver(envio.id())` — pasa el id, no el envío ni pide el objeto `Alerta` de vuelta.

En ningún punto del código de `envios/` aparece una clase `Alerta`, `Sensor` o `Lectura`. Es lo que debe verificar quien revise un Pull Request entre subdominios (sección 7 del taller): si alguna vez aparece un `import` de esas clases dentro de `envios/`, es una señal de que el límite del Agregado se rompió.
