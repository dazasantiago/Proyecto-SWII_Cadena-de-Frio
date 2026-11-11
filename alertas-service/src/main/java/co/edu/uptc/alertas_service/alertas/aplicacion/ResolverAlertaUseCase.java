package co.edu.uptc.alertas_service.alertas.aplicacion;

import co.edu.uptc.alertas_service.alertas.dominio.Alerta;
import java.util.UUID;

/**
 * Puerto primario: marcar una alerta como resuelta (evento {@code AlertaResuelta}).
 * Sin este caso de uso, un envío con una alerta crítica nunca podría cerrarse.
 */
public interface ResolverAlertaUseCase {

    Alerta resolver(UUID alertaId);
}
