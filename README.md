# Sistema de Cadena de Frío (G6)

Vigila envíos refrigerados: cada envío tiene un rango de temperatura y humedad, las lecturas fuera
de rango generan alertas y un envío no se puede cerrar mientras tenga alertas críticas sin resolver.
La arquitectura está en [`Docs/ARQUITECTURA.md`](Docs/ARQUITECTURA.md) y los documentos de cierre
en [`Docs/cierre/`](Docs/cierre/).

| Servicio | Carpeta | Puerto | Base de datos (Docker) |
|---|---|---|---|
| Envíos (incluye Lecturas de sensores) | `cadena-de-frio-g6/` | 8080 | PostgreSQL `cadena-db` en el 5435 |
| Alertas | `alertas-service/` | 8082 | PostgreSQL `alertas-db` en el 5436, Kafka `cadena-kafka` en el 9094 |
| Registro de servicios (Eureka) | `eureka-server/` | 8761 | — |

## Requisitos

- Java 25 (`java -version` debe mostrar 25 o superior). No hace falta instalar Maven: cada servicio trae `mvnw`.
- Docker Desktop corriendo.

## Levantar el sistema

Abre tres terminales, una por servicio, desde la raíz del repositorio y arráncalas en este orden.
En Windows usa `mvnw.cmd` en lugar de `./mvnw`.

**Terminal 1 — Registro de servicios (Eureka):**

```bash
cd eureka-server
./mvnw spring-boot:run
```

**Terminal 2 — Alertas** (levanta su base y Kafka, y luego el servicio):

```bash
cd alertas-service
docker compose up -d
./mvnw spring-boot:run
```

**Terminal 3 — Envíos:**

```bash
cd cadena-de-frio-g6
docker compose up -d
./mvnw spring-boot:run
```

Cada servicio termina de arrancar cuando su log muestra `Started ...Application`.

## Comprobar que funciona

```bash
curl http://localhost:8082/actuator/health
```

Debe responder `{"status":"UP", ...}` con el componente `db` en `UP`.

En el navegador, http://localhost:8761 muestra el panel de Eureka con `ALERTAS-SERVICE` y
`CADENA-DE-FRIO-G6` registrados en `UP` (tarda unos 30 segundos en aparecer).

### Petición que cruza dos servicios: cerrar un envío

Envíos no cierra un envío sin preguntarle antes a Alertas si tiene alertas críticas sin resolver.

```bash
# 1. Registrar un envío (Envíos) y ponerlo en tránsito
curl -X POST http://localhost:8080/api/envios -H "Content-Type: application/json" \
  -d '{"id":"44444444-4444-4444-4444-444444444444","temperaturaMinima":2,"temperaturaMaxima":8,"humedadMinima":40,"humedadMaxima":80}'
curl -X POST http://localhost:8080/api/envios/44444444-4444-4444-4444-444444444444/iniciar-transporte

# 2. Generar una alerta crítica para ese envío (Alertas). Guarda el "id" que devuelve.
curl -X POST http://localhost:8082/api/alertas -H "Content-Type: application/json" \
  -d '{"envioId":"44444444-4444-4444-4444-444444444444","sensorId":"22222222-2222-2222-2222-222222222222","lecturasFueraDeRangoConsecutivas":3}'

# 3. Intentar cerrar el envío: Envíos le pregunta a Alertas → 409 "tiene alertas críticas sin resolver"
curl -X POST http://localhost:8080/api/envios/44444444-4444-4444-4444-444444444444/cerrar

# 4. Resolver la alerta (Alertas) y volver a cerrar → 200, "estado":"CERRADO"
curl -X PATCH http://localhost:8082/api/alertas/<id-de-la-alerta>/resolver
curl -X POST http://localhost:8080/api/envios/44444444-4444-4444-4444-444444444444/cerrar
```

Si `alertas-service` está apagado, el paso 3 responde **503**: el envío nunca se cierra sin verificar.

### Otras pruebas de Alertas

Generar una alerta crítica (3 lecturas seguidas fuera de rango) y consultarla:

```bash
curl -X POST http://localhost:8082/api/alertas -H "Content-Type: application/json" \
  -d '{"envioId":"11111111-1111-1111-1111-111111111111","sensorId":"22222222-2222-2222-2222-222222222222","lecturasFueraDeRangoConsecutivas":3}'

curl http://localhost:8082/api/alertas/envios/11111111-1111-1111-1111-111111111111/criticas-sin-resolver
```

La segunda llamada responde `"hayCriticasSinResolver":true`. Para resolver la alerta:
`curl -X PATCH http://localhost:8082/api/alertas/<id-de-la-alerta>/resolver`.

Simular el evento que publica Lecturas de sensores por Kafka:

```bash
echo '{"envioId":"11111111-1111-1111-1111-111111111111","sensorId":"22222222-2222-2222-2222-222222222222","temperatura":9.5,"humedad":70.0,"fechaHora":"2026-11-10T10:00:00Z","lecturasFueraDeRangoConsecutivas":3}' \
  | docker exec -i cadena-kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9094 --topic sensores.lecturas-fuera-de-rango
```

## Pruebas

Con las bases levantadas (`docker compose up -d` en cada carpeta), desde la carpeta de cada servicio:

```bash
./mvnw test
```

## Detener todo

`Ctrl+C` en cada terminal y luego `docker compose down` en cada carpeta.
