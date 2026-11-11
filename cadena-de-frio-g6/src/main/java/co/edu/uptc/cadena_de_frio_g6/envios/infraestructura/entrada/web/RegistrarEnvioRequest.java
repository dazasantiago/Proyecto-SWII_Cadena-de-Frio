package co.edu.uptc.cadena_de_frio_g6.envios.infraestructura.entrada.web;

/**
 * Cuerpo de POST /api/envios. El id debe ser un UUID en texto: es el contrato de identidad con
 * alertas-service (ver ConsultaAlertasCriticasHttp).
 */
public record RegistrarEnvioRequest(String id, Double temperaturaMinima, Double temperaturaMaxima,
                                    Double humedadMinima, Double humedadMaxima) {

    void validar() {
        if (temperaturaMinima == null || temperaturaMaxima == null || humedadMinima == null || humedadMaxima == null) {
            throw new IllegalArgumentException(
                    "La condición requerida necesita temperaturaMinima, temperaturaMaxima, humedadMinima y humedadMaxima");
        }
    }
}
