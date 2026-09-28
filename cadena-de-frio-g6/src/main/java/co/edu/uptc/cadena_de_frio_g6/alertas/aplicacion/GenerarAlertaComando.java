package co.edu.uptc.cadena_de_frio_g6.alertas.aplicacion;

import java.util.UUID;

/**
 * Datos de entrada para {@link GenerarAlertaUseCase}: el envío y sensor de origen,
 * y el conteo de lecturas fuera de rango ya calculado por Lecturas de Sensores.
 */
public record GenerarAlertaComando(UUID envioId, UUID sensorId, int lecturasFueraDeRangoConsecutivas) {
}
