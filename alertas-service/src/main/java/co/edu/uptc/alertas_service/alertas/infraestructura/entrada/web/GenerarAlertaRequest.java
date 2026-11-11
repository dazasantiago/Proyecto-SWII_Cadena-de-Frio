package co.edu.uptc.alertas_service.alertas.infraestructura.entrada.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** Cuerpo de POST /api/alertas: de qué envío y sensor viene la lectura fuera de rango. */
public record GenerarAlertaRequest(
        @NotNull UUID envioId,
        @NotNull UUID sensorId,
        @NotNull @Min(0) Integer lecturasFueraDeRangoConsecutivas) {
}
