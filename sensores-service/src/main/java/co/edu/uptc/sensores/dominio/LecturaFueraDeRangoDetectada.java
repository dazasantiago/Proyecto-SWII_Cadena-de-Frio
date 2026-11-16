package co.edu.uptc.sensores.dominio;

import java.time.Instant;
import java.util.UUID;

/** Evento para alertas al incumplir la condición del envío. */
public record LecturaFueraDeRangoDetectada(UUID envioId, UUID sensorId, double temperatura,
        double humedad, Instant fechaHora, int lecturasFueraDeRangoConsecutivas) { }
