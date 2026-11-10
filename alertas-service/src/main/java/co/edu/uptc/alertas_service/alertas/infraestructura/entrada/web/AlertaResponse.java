package co.edu.uptc.alertas_service.alertas.infraestructura.entrada.web;

import co.edu.uptc.alertas_service.alertas.dominio.Alerta;
import java.time.Instant;
import java.util.UUID;

/** Lo que el servicio devuelve de una alerta: nunca expone la entidad de dominio directamente. */
public record AlertaResponse(UUID id, UUID envioId, UUID sensorId, Instant fechaGeneracion,
                             String severidad, boolean resuelta) {

    static AlertaResponse desde(Alerta alerta) {
        return new AlertaResponse(alerta.id(), alerta.envioId(), alerta.sensorId(),
                alerta.fechaGeneracion(), alerta.severidad().nivel(), alerta.resuelta());
    }
}
