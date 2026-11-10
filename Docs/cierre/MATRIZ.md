# Matriz de decisión ponderada

Comparamos tres arquitecturas reales para el sistema:

- **A — Monolito modular:** todo en `cadena-de-frio-g6`, con paquetes por subdominio (como estaba en septiembre).
- **B — Microservicios completos:** lo que dibujaba el C4 de agosto: ingesta, envíos, detección de
  alertas, notificaciones y consultas como servicios separados, con base de series de tiempo.
- **C — Híbrida (la nuestra):** Envíos como servicio hexagonal, Alertas extraído con su propia base,
  y Kafka solo donde hay volumen y hechos que propagar (lecturas fuera de rango).

## Pesos (de 1 a 5) y por qué

- **Integridad del cierre (5):** es el atributo 1. Cerrar un envío con la cadena de frío rota tiene
  consecuencias sanitarias y legales.
- **Costo de operar con 3 personas y un semestre (5):** es nuestra restricción más dura. Cada
  servicio extra es un proceso, una base y un despliegue más que mantener.
- **Disponibilidad de la detección de alertas (4):** perder una lectura fuera de rango es grave,
  pero un retraso corto es aceptable.
- **Modificabilidad de las reglas de alerta (3):** es lo que más cambia, aunque cambia poco a poco.
- **Escalabilidad del volumen de lecturas (3):** hoy las lecturas son simuladas; importa para el
  futuro, no para la entrega.
- **Testabilidad (3):** ya la tenemos bien resuelta con el dominio hexagonal en cualquier opción.

## Tabla

| Criterio | Peso | A: monolito modular | B: microservicios | C: híbrida (nuestra) |
|---|---|---|---|---|
| Integridad del cierre | 5 | 5 -> 25 | 3 -> 15 | 4 -> 20 |
| Costo de operar con 3 personas | 5 | 5 -> 25 | 1 -> 5 | 3 -> 15 |
| Disponibilidad de la detección | 4 | 2 -> 8 | 5 -> 20 | 4 -> 16 |
| Modificabilidad de las reglas de alerta | 3 | 2 -> 6 | 5 -> 15 | 4 -> 12 |
| Escalabilidad del volumen de lecturas | 3 | 2 -> 6 | 5 -> 15 | 4 -> 12 |
| Testabilidad | 3 | 4 -> 12 | 3 -> 9 | 4 -> 12 |
| **TOTAL** | | **82** | **79** | **87** |

Por qué esas puntuaciones: A tiene la integridad más fácil (una transacción local) y el menor
costo, pero si el monolito cae no se detecta nada y cambiar una regla obliga a redesplegar todo.
B es el mejor en disponibilidad y escalabilidad, pero cada consulta cruza varios servicios y su
costo de operación es inmanejable para tres personas. C obtiene casi lo mejor de las dos en los
atributos que más pesan.

## Análisis de sensibilidad

Movimos ±2 el peso de los dos criterios más importantes y recalculamos:

| Cambio de peso | A | B | C | Ganador |
|---|---|---|---|---|
| Integridad 5 → 7 | 92 | 85 | 95 | C |
| Integridad 5 → 3 | 72 | 73 | 79 | C |
| Costo de operar 5 → 7 | 92 | 81 | 93 | C (por 1 punto) |
| Costo de operar 5 → 3 | 72 | 77 | 81 | C |

Con un solo ajuste el ganador no cambia, pero **subiendo el peso del costo de operar a 7, C gana
por un solo punto**. Si además bajamos la disponibilidad de la detección de 4 a 2, gana el
monolito modular (A: 88, C: 85). La decisión es frágil justo en el criterio que peor conocemos:
cuánto nos cuesta de verdad operar dos servicios. Para reducir esa incertidumbre mediríamos
el tiempo que tarda una persona nueva en levantar el sistema completo siguiendo el README, y las
horas por semana que dedicamos a problemas de infraestructura en lugar de funcionalidad.

## Conclusión

Elegimos **C** porque es la única opción que protege a la vez la integridad del cierre y la
detección de alertas aunque un servicio caiga, aceptando el costo de operar un servicio más, una
base más y un broker de Kafka. Si las mediciones mostraran que ese costo es mayor de lo que
estimamos, volveríamos a meter Alertas en el monolito como módulo (opción A) antes que crecer
hacia la opción B.
