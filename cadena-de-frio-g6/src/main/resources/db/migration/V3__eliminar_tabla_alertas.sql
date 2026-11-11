-- Alertas se extrajo a alertas-service, con su propia base de datos (Database per Service).
-- Este servicio ya no guarda alertas: le pregunta a alertas-service por HTTP.
-- (No se edita la V2 porque una migración aplicada nunca se modifica.)
DROP TABLE IF EXISTS alertas;
