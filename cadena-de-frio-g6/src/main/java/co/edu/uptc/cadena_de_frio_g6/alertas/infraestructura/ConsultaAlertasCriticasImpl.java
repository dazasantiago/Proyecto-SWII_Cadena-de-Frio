package co.edu.uptc.cadena_de_frio_g6.alertas.infraestructura;

import java.util.UUID;

import org.springframework.stereotype.Component;

import co.edu.uptc.cadena_de_frio_g6.alertas.aplicacion.ConsultaAlertasCriticasUseCase;
import co.edu.uptc.cadena_de_frio_g6.envios.aplicacion.ConsultaAlertasCriticas;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;

/**
 * Adaptador entre contextos: implementa el puerto que Envíos define ({@link ConsultaAlertasCriticas})
 * delegando en el puerto primario de Alertas ({@link ConsultaAlertasCriticasUseCase}). Cruza el
 * límite solo por id: nunca recibe ni entrega una {@code Alerta}.
 *
 * <p>Contrato de identidad acordado entre Envíos y Alertas: {@code EnvioId.valor()} es la
 * representación en texto del mismo UUID que Alertas guarda como {@code envioId}.</p>
 */
@Component
public class ConsultaAlertasCriticasImpl implements ConsultaAlertasCriticas {

    private final ConsultaAlertasCriticasUseCase consultaAlertasCriticas;

    public ConsultaAlertasCriticasImpl(ConsultaAlertasCriticasUseCase consultaAlertasCriticas) {
        this.consultaAlertasCriticas = consultaAlertasCriticas;
    }

    @Override
    public boolean tieneAlertasCriticasSinResolver(EnvioId envioId) {
        return consultaAlertasCriticas.hayAlertaCriticaSinResolver(convertir(envioId));
    }

    private UUID convertir(EnvioId envioId) {
        try {
            return UUID.fromString(envioId.valor());
        } catch (IllegalArgumentException ex) {
            throw new EnvioIdNoEsUuidException(envioId);
        }
    }
}
