# Evaluación ligera de la arquitectura (ATAM-lite)

Partimos de los atributos de calidad de la sección 1 de `Docs/ARQUITECTURA.md`.

## 1. Árbol de utilidad

Cada escenario lleva dos letras: importancia para el negocio y dificultad para lograrlo
(A = alta, M = media, B = baja).

| Atributo | Escenario (estímulo → respuesta → medida) | Importancia / dificultad |
|---|---|---|
| Integridad | Un operador intenta cerrar un envío con una alerta crítica sin resolver mientras `alertas-service` está caído → el cierre se rechaza → 0 envíos cerrados sin verificación (respuesta 503). | **A / A** |
| Oportunidad de la alerta | Llega un evento `LecturaFueraDeRangoDetectada` a Kafka → se genera la alerta → en menos de 2 s en condiciones normales y siempre en menos de 5 min (HU-03). | **A / M** |
| Disponibilidad de la detección | `alertas-service` se cae 10 minutos mientras los sensores siguen enviando → al volver procesa todo lo pendiente → 0 lecturas fuera de rango perdidas. | **A / M** |
| Integridad ante duplicados | Kafka reentrega todos los eventos tras un reinicio → no se crean alertas nuevas → el número de alertas no cambia. | **A / M** |
| Modificabilidad | Se cambia la regla "3 lecturas seguidas → crítica" a 5 → se edita una clase de dominio → sin tocar ni desplegar Envíos. | M / B |
| Testabilidad | Un desarrollador cambia la regla de severidad → corre las pruebas de dominio → en menos de 10 s, sin base de datos. | M / B |

## 2. Análisis de los escenarios A/A y A/M

**Integridad (A/A).** Lo sostiene ADR-003: `ConsultaAlertasCriticasHttp` con Circuit Breaker y
*fail-closed*. Qué podría fallar: si alguien cambiara el adaptador a "si no responde, permite el
cierre", el escenario se rompe sin que ninguna prueba de dominio lo note.

**Oportunidad de la alerta (A/M).** Lo sostienen Kafka y el consumidor de `alertas-service`
(ADR-002). Qué podría fallar: hoy sensores no publica en Kafka (los eventos se simulan), así que
el escenario completo de punta a punta no se ha medido con el productor real.

**Disponibilidad de la detección (A/M).** Lo sostiene que Kafka conserva los eventos y que el
consumidor retoma desde su último offset. Qué podría fallar: hay un solo broker; si se cae Kafka
(no Alertas), la detección se detiene.

**Integridad ante duplicados (A/M).** Lo sostiene la tabla `eventos_procesados`, escrita en la
misma transacción que la alerta. Lo probamos: al reentregar todos los eventos, la base siguió con
2 alertas y el log mostró "ya procesado, se ignora".

## 3. Clasificación de lo encontrado

| Tipo | Decisión | Efecto |
|---|---|---|
| **Riesgo** | Lecturas de sensores todavía no publica en Kafka y su evento no trae `lecturasFueraDeRangoConsecutivas`. | Ninguna alerta escala a crítica en el flujo real: el escenario de integridad pierde su origen. |
| **Riesgo** | Un solo broker de Kafka sin réplicas. | Punto único de falla para la detección. |
| **Riesgo** | Nadie vigila la Dead Letter Topic. | Un evento válido que falle por un error transitorio prolongado queda guardado pero olvidado. |
| **Riesgo** | Envíos todavía no tiene controlador REST para cerrar envíos. | El escenario de integridad solo está probado con pruebas unitarias del adaptador, no de punta a punta. |
| **No-riesgo** | Idempotencia en la misma transacción que la alerta. | Un evento repetido nunca duplica alertas ni deja una alerta sin registrar. |
| **No-riesgo** | Database per Service. | Un cambio de esquema en Envíos no puede romper a Alertas. |
| **Sensibilidad** | Parámetros del Circuit Breaker (ventana 10, umbral 50 %, 10 s abierto) y plazos HTTP (2 s / 3 s). | Con plazos más largos el cierre se siente colgado; con un umbral muy bajo el circuito se abre por un solo error. |
| **Sensibilidad** | Umbral de escalamiento (3 lecturas seguidas). | Define cuántas alertas críticas bloquean cierres: muy bajo genera falsas críticas, muy alto retrasa la reacción. |
| **Compromiso** | *Fail-closed* en el cierre (ADR-003). | Mejora la integridad y empeora la disponibilidad del cierre: con Alertas caído no se cierra ningún envío. |
| **Compromiso** | Kafka entre sensores y alertas (ADR-002). | Mejora disponibilidad y desacoplamiento, a cambio de consistencia eventual (la alerta llega un instante después). |

## 4. Los 3 riesgos principales y su mitigación

1. **Riesgo: el productor real de eventos no existe todavía** (sensores no publica en Kafka y el
   evento no trae el conteo de lecturas seguidas).
   **Mitigación:** extraer `sensores-service` y publicar el evento con un Outbox, agregando el campo
   `lecturasFueraDeRangoConsecutivas`. Mientras tanto, el consumidor es un lector tolerante (si el
   campo falta, cuenta 1) y el contrato está documentado en `ARQUITECTURA.md`.
2. **Riesgo: Kafka es un punto único de falla.**
   **Mitigación:** aceptarlo en el entorno del curso (los eventos ya escritos no se pierden); en
   producción, 3 brokers con factor de replicación 3.
3. **Riesgo: la DLT no se vigila.**
   **Mitigación:** exponer el número de mensajes en `sensores.lecturas-fuera-de-rango.DLT` como
   métrica y crear una alerta cuando sea mayor que cero; el reprocesamiento lo decide una persona
   después de revisar la causa en las cabeceras.
