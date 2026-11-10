# Gobernanza: guardarraíles y toma de decisiones

## Guardarraíles de arquitectura

1. Regla: el dominio no importa clases de Spring ni de infraestructura (los paquetes `dominio` de
   `alertas-service` y de `cadena-de-frio-g6` solo usan Java y, como excepción tolerada, anotaciones
   `jakarta.persistence`).
   Se verifica con: `grep -rl "import org.springframework" */src/main/java/**/dominio` debe salir vacío
   (o una prueba ArchUnit equivalente).
   Cuándo: en cada push, como paso del CI.
2. Regla: ningún servicio usa el código ni la base de datos de otro.
   Se verifica con: `grep -r "cadena_de_frio_g6" alertas-service/src` y
   `grep -r "alertas_service" cadena-de-frio-g6/src` deben salir vacíos; cada servicio tiene su propio
   `docker-compose.yml` con su base.
   Cuándo: en cada push (CI) y al revisar cada Pull Request.
3. Regla: el esquema de cada base lo cambia solo Flyway; una migración aplicada nunca se edita.
   Se verifica con: `spring.jpa.hibernate.ddl-auto=validate` en los dos servicios; la prueba de
   arranque (`contextLoads`) falla si una entidad no coincide con su migración.
   Cuándo: en cada `mvnw test`, local y en el CI.
4. Regla: todo consumidor de Kafka es idempotente y tiene Dead Letter Topic.
   Se verifica con: revisión del Pull Request (lista de chequeo) y una prueba unitaria de "evento
   ya procesado" por consumidor, como `ProcesadorLecturasFueraDeRangoTest`.
   Cuándo: al revisar cada Pull Request que agregue un `@KafkaListener`.

## Cómo se toman las decisiones

- Decisiones locales (dentro de un subdominio): las toma quien lo implementa y avisa en el grupo.
- Decisiones que cruzan fronteras (eventos, APIs, bases de datos): se escribe un ADR en
  `Docs/ARQUITECTURA.md` y lo revisan al menos 2 personas antes de fusionar a `main`. Ejemplo: el
  contrato del evento `LecturaFueraDeRangoDetectada` entre sensores y alertas.
- Todo cambio entra a `main` por Pull Request desde una rama propia (`dev3/alertas-service`,
  `Lectura-Sensores`, …).
- Las excepciones a una regla se registran como deuda, con fecha de revisión.

## Deuda técnica reconocida

| Deuda | Por qué se aceptó | Riesgo | Revisión |
|---|---|---|---|
| Lecturas de sensores sigue dentro de `cadena-de-frio-g6` y no publica en Kafka | Se priorizó extraer Alertas, que tenía la regla de negocio más crítica | El flujo real de alertas no funciona de punta a punta | Al cerrar el semestre |
| El evento no trae `lecturasFueraDeRangoConsecutivas` | El contrato se definió después de que sensores escribiera su evento | Ninguna alerta escala a crítica en el flujo real | Antes de integrar sensores |
| El `.gitignore` raíz es una lista blanca: toda carpeta nueva queda ignorada hasta agregarla | Se configuró al inicio para no subir archivos del IDE | Que un servicio o el flujo de CI no se suban sin que nadie lo note | Ya; reemplazarlo por un `.gitignore` normal |
| Nadie vigila la DLT | No hay herramienta de monitoreo en el curso | Eventos válidos olvidados en la DLT | Cuando se agregue observabilidad (métricas y alertas) |

## Radar tecnológico mínimo

- **Adoptamos:** PostgreSQL con Flyway (esquema versionado y validado), y Kafka para propagar
  hechos de alto volumen, porque ya los probamos en el proyecto y en los talleres.
- **Evaluaríamos:** TimescaleDB para el histórico de lecturas, porque las consultas por rango de
  tiempo sobre millones de lecturas no son el fuerte de una tabla normal.
- **Evitaríamos:** un service mesh y Kubernetes por ahora: con 2 o 3 servicios el costo de
  aprenderlos y operarlos supera lo que resuelven.
