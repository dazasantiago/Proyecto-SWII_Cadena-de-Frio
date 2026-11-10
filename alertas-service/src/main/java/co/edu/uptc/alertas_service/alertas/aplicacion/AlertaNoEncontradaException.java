package co.edu.uptc.alertas_service.alertas.aplicacion;

import java.util.UUID;

public class AlertaNoEncontradaException extends RuntimeException {

    public AlertaNoEncontradaException(UUID alertaId) {
        super("No existe una alerta con id " + alertaId);
    }
}
