package co.edu.uptc.cadena_de_frio_g6.sensores.dominio;

import java.time.Instant;
import java.util.UUID;

/** Evento publicado cuando una lectura incumple la condición requerida del envío. */
public record LecturaFueraDeRangoDetectada(
        UUID envioId, UUID sensorId, double temperatura, double humedad, Instant fechaHora) {
}
