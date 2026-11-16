# sensores-service

Servicio Spring Boot para recibir lecturas en `POST /api/lecturas` y enviar a Kafka los eventos que incumplen los rangos del envío.

## Configuración

- Puerto HTTP: `8083`.
- Kafka: `KAFKA_BOOTSTRAP_SERVERS` (por defecto `localhost:9094`).
- Tópico: `sensores.kafka.topic` (por defecto `sensores.lecturas-fuera-de-rango`).
- Tiempo máximo de publicación: `SENSORES_KAFKA_TIMEOUT_MS` (por defecto `5000`).
- Archivo del contador: `SENSORES_CONTADOR_PATH` (por defecto `./data/lecturas-consecutivas.properties`).

El directorio de datos debe montarse en un volumen para conservar los contadores al reemplazar el contenedor. Por ejemplo, en Docker se puede montar un volumen nombrado en `/app/data`.

El almacenamiento en archivo admite una sola instancia del servicio escribiendo en ese directorio. No se debe compartir el archivo entre varias réplicas concurrentes.

## Solicitud de lectura

Cuando `envioId` está presente, el cuerpo debe incluir los cuatro límites de temperatura y humedad. Los valores de lectura y los límites deben ser finitos; los límites mínimo y máximo no pueden estar invertidos. Una solicitud inválida responde `400`. Si Kafka no confirma la publicación antes del timeout o falla, la API responde `503`.
