package co.edu.uptc.sensores.dominio;

import java.util.UUID;

/** Resultado de ingesta cuando la lectura no puede asociarse a un envío. */
public record LecturaDescartada(UUID sensorId, String motivo) { }
