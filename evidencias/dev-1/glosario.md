# Paso 1 · Lenguaje Ubicuo — Subdominio Envíos y condiciones

**Dev 1** · Bounded Context: Gestión de Envíos · Raíz del Agregado: `Envio`

## 1. Términos del subdominio

| Término | Qué significa en este contexto | Origen | Nombre en código |
|---|---|---|---|
| **Envío** | Traslado de una mercancía sensible, en un contenedor refrigerado, de un origen a un destino. Es la raíz del Agregado: todo lo demás de este contexto se accede a través de él. | HU-01, HU-08 | `Envio` |
| **Contenedor** | Unidad refrigerada en la que viaja la carga durante el envío. En este contexto solo interesa **cuál** contenedor lleva el envío, no sus sensores. | Entidades clave | `ContenedorId` (referencia por id) |
| **Condición requerida** | Lo que debe cumplirse durante todo el transporte: el rango de temperatura y el rango de humedad aceptables. Define qué se considera una falla. **No se puede modificar una vez iniciado el transporte.** | HU-01, regla 3 | `CondicionRequerida` |
| **Rango de temperatura** | Temperatura mínima y máxima permitidas, en °C. El mínimo debe ser menor que el máximo. | HU-01 | `RangoTemperatura` |
| **Rango de humedad** | Humedad relativa mínima y máxima permitidas, en %. | HU-01 | `RangoHumedad` |
| **Registrar envío** | Dar de alta un envío con su condición requerida, antes de que salga. Lo hace el operador logístico. | HU-01 | `EnvioFactory.crear(...)`, evento `EnvioRegistrado` |
| **Iniciar transporte** | Momento en que el envío sale hacia el destino. Desde aquí la condición requerida queda bloqueada. | Regla 3 | `Envio.iniciarTransporte()`, evento `EnvioIniciado` |
| **Cerrar envío** | Dar por terminado el envío al llegar a destino, con su resumen de cumplimiento. Solo es posible si no hay alertas críticas sin resolver. | HU-08, regla 2 | `Envio.cerrar(...)`, evento `EnvioCerrado` |
| **Resumen de cumplimiento** | Constancia formal, al cierre, de si las condiciones se cumplieron durante el transporte. | HU-08 | `ResumenCumplimiento` |
| **Incidente** | Hecho que documenta la causa de una posible falla, reportado por el operador sobre un envío. Puede reportarse en cualquier punto del transporte. | HU-09 | `Incidente`, evento `IncidenteReportado` |
| **Operador logístico** | Actor que registra, cierra y reporta incidentes de un envío. | Actores | `OperadorId` (referencia por id) |
| **Cliente** | Dueño de la carga. En este contexto solo interesa **quién es**, para asociarlo al envío. | Actores, HU-05 | `ClienteId` (referencia por id) |

## 2. Ciclo de vida del envío

| Estado | Significa | Se pasa al siguiente con |
|---|---|---|
| `REGISTRADO` | Existe con su condición requerida; aún no sale. La condición todavía puede ajustarse. | `iniciarTransporte()` |
| `EN_TRANSITO` | Está en camino. La condición requerida es inmutable. Admite incidentes. | `cerrar(...)` |
| `CERRADO` | Terminó. Tiene resumen de cumplimiento. No admite más cambios. | — |

## 3. Términos de otros contextos (no viven aquí)

Estos conceptos existen en el sistema, pero pertenecen a otro Bounded Context. Envíos solo los referencia **por id** o los consulta a través de una interfaz.

| Término | Contexto dueño | Cómo aparece en Envíos |
|---|---|---|
| **Lectura** (dato de temperatura/humedad con marca de tiempo) | Lecturas de Sensores | No aparece. Envíos no guarda ni procesa lecturas. |
| **Sensor** | Lecturas de Sensores | No aparece. |
| **Alerta** y **severidad crítica** | Alertas e Incidentes | Solo mediante la pregunta "¿tiene este envío alertas críticas sin resolver?", que `CierreEnvioService` hace a través de una interfaz (`ConsultaAlertasCriticas`). |

## 4. Términos que evitamos

| Término | Por qué se evita |
|---|---|
| **"estado"** a secas | Es ambiguo: en Envíos es el ciclo de vida (`REGISTRADO`/`EN_TRANSITO`/`CERRADO`), y en otros contextos podría significar el estado de una alerta o de un sensor. Se dice **estado del envío**. |
| **"condición"** a secas | Se confunde con condiciones de código o con "condiciones ambientales" en general. Se dice **condición requerida**. |
| **"viaje"**, **"ruta"**, **"pedido"** | No aparecen en los requisitos y sugieren otros conceptos. El término del dominio es **envío**; "transporte" nombra solo la fase en tránsito. |
| **"lectura"**, **"alerta"** como parte del envío | Pertenecen a otros contextos. Un `Envio` nunca contiene una lista de lecturas ni de alertas. |

## 5. Decisiones que aún hay que confirmar

Los términos marcados aquí no vienen literalmente de los requisitos; son supuestos míos para dejar el modelo coherente.

- **`EnvioIniciado` / `iniciarTransporte()`** no aparece en `NOTAS-equipo.md`. Hace falta porque la regla 3 habla de "una vez iniciado el transporte". Habría que añadirlo a la lista de eventos del equipo.
- **`RangoHumedad`** como Value Object separado de `RangoTemperatura`: HU-01 pide ambos rangos, pero el equipo solo nombró el de temperatura.
- **`ResumenCumplimiento`**: los requisitos no dicen qué contiene. Su contenido depende de cómo se cruce con Alertas y Lecturas.
- **`Incidente`** dentro o fuera del Agregado `Envio`: pendiente para el paso 4.
