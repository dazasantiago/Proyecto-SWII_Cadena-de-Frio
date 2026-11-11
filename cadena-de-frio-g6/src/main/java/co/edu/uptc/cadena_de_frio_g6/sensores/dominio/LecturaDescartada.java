package co.edu.uptc.cadena_de_frio_g6.sensores.dominio;

import java.util.UUID;

/** Resultado de ingesta cuando la lectura no puede asociarse a un envío. */
public record LecturaDescartada(UUID sensorId, String motivo) {
}
