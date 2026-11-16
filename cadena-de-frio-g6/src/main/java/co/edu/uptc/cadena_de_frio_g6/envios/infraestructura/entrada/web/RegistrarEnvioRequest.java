package co.edu.uptc.cadena_de_frio_g6.envios.infraestructura.entrada.web;

import java.util.UUID;

/**
 * Cuerpo de POST /api/envios. El id debe ser un UUID en texto: es el contrato de identidad con
 * alertas-service (ver ConsultaAlertasCriticasHttp).
 */
public record RegistrarEnvioRequest(String id, Double temperaturaMinima, Double temperaturaMaxima,
                                    Double humedadMinima, Double humedadMaxima) {

    void validar() {
        if (!esUuidCanonico(id)) {
            throw new IllegalArgumentException("El id del envío debe ser un UUID en minúsculas con el formato "
                    + "8-4-4-4-12 (p. ej. 44444444-4444-4444-4444-444444444444): Alertas identifica los envíos por UUID");
        }
        if (temperaturaMinima == null || temperaturaMaxima == null || humedadMinima == null || humedadMaxima == null) {
            throw new IllegalArgumentException(
                    "La condición requerida necesita temperaturaMinima, temperaturaMaxima, humedadMinima y humedadMaxima");
        }
    }

    /**
     * Solo la forma canónica: UUID.fromString también acepta "1-1-1-1-1" o mayúsculas, y dos textos
     * distintos serían dos envíos en Envíos pero el mismo envío para Alertas.
     */
    private static boolean esUuidCanonico(String valor) {
        if (valor == null) {
            return false;
        }
        try {
            return UUID.fromString(valor).toString().equals(valor);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
