-- Tabla del adaptador JPA de Alertas (AlertaJpaEntity). Hibernate corre con
-- ddl-auto=validate, así que el esquema tiene que venir de Flyway.
CREATE TABLE alertas (
    id               UUID PRIMARY KEY,
    envio_id         UUID         NOT NULL,
    sensor_id        UUID         NOT NULL,
    fecha_generacion TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    severidad        VARCHAR(255) NOT NULL,
    resuelta         BOOLEAN      NOT NULL
);

CREATE INDEX idx_alertas_envio ON alertas (envio_id);
