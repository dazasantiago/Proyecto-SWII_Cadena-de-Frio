package co.edu.uptc.cadena_de_frio_g6.alertas;

import java.util.UUID;

import org.springframework.stereotype.Component;

import co.edu.uptc.cadena_de_frio_g6.envios.ConsultaAlertasCriticas;
import co.edu.uptc.cadena_de_frio_g6.envios.EnvioId;

/**
 * Implementa el puerto que Envíos define ({@link ConsultaAlertasCriticas}) con los datos de
 * Alertas. Cruza el límite de contexto solo por id: nunca recibe ni entrega un {@link Alerta}.
 *
 * <p>Contrato de identidad acordado entre Envíos y Alertas para esta entrega:
 * {@code EnvioId.valor()} es la representación en texto del mismo UUID que Alertas guarda como
 * {@code envioId}. Es la pieza que faltaba para que {@code CierreEnvioService} (Envíos) deje de
 * depender de un mock.</p>
 */
@Component
public class ConsultaAlertasCriticasImpl implements ConsultaAlertasCriticas {

    private final AlertaRepository alertaRepository;

    public ConsultaAlertasCriticasImpl(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @Override
    public boolean tieneAlertasCriticasSinResolver(EnvioId envioId) {
        UUID envioUuid = convertir(envioId);
        return alertaRepository.buscarPorEnvio(envioUuid).stream()
                .anyMatch(alerta -> alerta.esCritica() && !alerta.resuelta());
    }

    private UUID convertir(EnvioId envioId) {
        try {
            return UUID.fromString(envioId.valor());
        } catch (IllegalArgumentException ex) {
            throw new EnvioIdNoEsUuidException(envioId);
        }
    }
}
