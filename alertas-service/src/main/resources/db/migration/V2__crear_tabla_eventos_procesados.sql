-- Consumidor idempotente: un registro por evento de Kafka ya procesado.
-- La clave primaria hace que el mismo evento no se pueda registrar dos veces.
CREATE TABLE eventos_procesados (
    id            VARCHAR(200) PRIMARY KEY,
    procesado_en  TIMESTAMP(6) WITH TIME ZONE NOT NULL
);
