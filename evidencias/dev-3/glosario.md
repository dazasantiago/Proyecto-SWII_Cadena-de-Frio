# Paso 1 · Lenguaje Ubicuo — Subdominio Alertas e Incidentes

**Dev 3** · Bounded Context: Alertas e Incidentes · Raíz del Agregado: `Alerta`

## 1. Términos del subdominio

| Término | Qué significa en este contexto | Origen | Nombre en código |
|---|---|---|---|
| **Alerta** | Aviso generado cuando una lectura de sensor incumple la condición requerida de un envío. Es la raíz del Agregado: todo lo demás de este contexto se accede a través de ella. | HU-03 | `Alerta` |
| **Severidad** | Nivel de gravedad de una alerta: `NORMAL` o `CRITICA`. Empieza en `NORMAL` y solo escala a `CRITICA`, nunca al revés. | HU-03, regla 5 | `SeveridadAlerta` |
| **Escalamiento** | Proceso por el cual una alerta pasa de severidad normal a crítica, al detectarse varias lecturas fuera de rango consecutivas para el mismo envío. | HU-03, regla 5 | `EscalamientoAlertaService` |
| **Lectura fuera de rango** | Dato de un sensor que no cumple la condición requerida del envío. Alertas no la produce ni la guarda; solo la consume para decidir si genera o escala una alerta. | HU-03, evento pivote `LecturaFueraDeRangoDetectada` | Parámetro de entrada (conteo), no una entidad propia |
| **Generar alerta** | Crear una alerta nueva, en severidad normal, en menos de 5 minutos desde la lectura fuera de rango que la origina. | HU-03, criterio de aceptación | `AlertaFactory.generar(...)`, evento `AlertaGenerada` |
| **Notificar alerta** | Avisar al operador y al cliente de una alerta existente. Notificaciones es una capacidad externa genérica: Alertas solo produce el evento, no despacha el aviso. | HU-04, HU-05 | evento `OperadorNotificado` / `ClienteNotificado` (consumidos por Notificaciones) |
| **Resolver alerta** | Cerrar el ciclo de vida de una alerta cuando la condición que la originó ya no aplica. Necesaria para que Envíos pueda cerrar el envío (HU-08). | Supuesto (no está en `NOTAS-equipo.md`, ver §5) | `Alerta.resolver()`, evento `AlertaResuelta` |
| **Envío** | El transporte cuya condición requerida se está vigilando. Alertas nunca lo carga completo, solo su identificador. | HU-01 (otro contexto) | referencia por id (`UUID envioId`) |
| **Sensor** | El dispositivo que produjo la lectura que originó la alerta. Alertas nunca lo carga completo, solo su identificador. | HU-02 (otro contexto) | referencia por id (`UUID sensorId`) |

## 2. Ciclo de vida de la alerta

| Estado | Significa | Se pasa al siguiente con |
|---|---|---|
| `GENERADA` (severidad `NORMAL`) | Existe, referencia su envío y sensor de origen. Aún no se resolvió. | `EscalamientoAlertaService` (si se acumulan lecturas fuera de rango) o `resolver()` |
| `GENERADA` (severidad `CRITICA`) | Escaló porque hubo varias lecturas fuera de rango consecutivas. Bloquea el cierre del envío (HU-08) mientras no se resuelva. | `resolver()` |
| `RESUELTA` | La condición que la originó ya no aplica. No admite más cambios de severidad. | — |

## 3. Términos de otros contextos (no viven aquí)

Estos conceptos existen en el sistema, pero pertenecen a otro Bounded Context. Alertas solo los referencia **por id** o los consume como dato de entrada.

| Término | Contexto dueño | Cómo aparece en Alertas |
|---|---|---|
| **Envío** y **condición requerida** | Gestión de Envíos | Solo como `envioId` (UUID). Alertas nunca lee ni valida el rango de temperatura directamente. |
| **Sensor** y **Lectura** | Lecturas de Sensores | Solo como `sensorId` (UUID) y como un conteo de lecturas fuera de rango consecutivas que recibe `EscalamientoAlertaService`. Alertas no guarda el historial de lecturas. |
| **Incidente** | Gestión de Envíos (ver §5 — no Alertas, a pesar del nombre del Bounded Context) | No aparece en el modelo de `Alerta`. |

## 4. Términos que evitamos

| Término | Por qué se evita |
|---|---|
| **"estado"** a secas | Ambiguo entre el ciclo de vida de la alerta (`GENERADA`/`RESUELTA`) y su severidad (`NORMAL`/`CRITICA`). Son dos ejes distintos: una alerta puede estar generada-normal, generada-crítica o resuelta. Se dice **severidad** para un eje y **resuelta/sin resolver** para el otro. |
| **"prioridad"** | No aparece en los requisitos; el término del dominio es **severidad**. |
| **"lectura"**, **"condición requerida"** como parte de la alerta | Pertenecen a otros contextos. Una `Alerta` nunca contiene una `Lectura` ni un `RangoTemperatura`, solo ids y un conteo. |

## 5. Decisiones que aún hay que confirmar

Los términos marcados aquí no vienen literalmente de los requisitos; son supuestos míos para dejar el modelo coherente.

- **Nombre del Bounded Context ("Alertas e Incidentes") vs. dueño real de `Incidente`.** En `NOTAS-equipo.md` §2.1 y en el diagrama de eventos, `IncidenteReportado` (HU-09) queda agrupado bajo `BC1 — Gestión de Envíos · Dev 1`, no bajo `BC3 — Alertas e Incidentes · Dev 3`. Este glosario asume que `Incidente` **no** es parte del agregado `Alerta` y que el nombre del contexto es heredado/aspiracional. Pendiente confirmar con el equipo si esto va a cambiar.
- **`Alerta.resolver()` / evento `AlertaResuelta`** no aparece explícito en la lista de eventos de `NOTAS-equipo.md` más que como fila `8 · Supuesto`. Lo mantengo porque sin él, `CierreEnvioService` (Dev 1) no tendría forma de que una alerta crítica deje de bloquear el cierre del envío.
- **Umbral de lecturas consecutivas para escalar a crítica.** Ni `NOTAS-equipo.md` ni las HU dan un número. `EscalamientoAlertaService` usa `3` como valor por defecto (ver Paso 3) — a confirmar con el equipo/docente.
- **`envioId`/`sensorId` como `UUID` crudo**, en vez de un Value Object propio (`EnvioId`, `SensorId`). Se prefirió lo más simple posible para esta entrega; si el equipo decide que esos ids necesitan validación propia (formato, prefijo, etc.), se puede envolver más adelante sin tocar el resto del agregado.
