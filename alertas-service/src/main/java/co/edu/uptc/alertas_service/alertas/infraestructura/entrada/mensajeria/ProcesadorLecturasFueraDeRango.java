package co.edu.uptc.alertas_service.alertas.infraestructura.entrada.mensajeria;

import co.edu.uptc.alertas_service.alertas.aplicacion.GenerarAlertaComando;
import co.edu.uptc.alertas_service.alertas.aplicacion.GenerarAlertaUseCase;
import co.edu.uptc.alertas_service.alertas.dominio.Alerta;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Traduce el evento LecturaFueraDeRangoDetectada (publicado por Lecturas de Sensores) en una
 * alerta, de forma idempotente: el registro del evento procesado y la alerta se guardan en la
 * MISMA transacción, así que nunca queda uno sin el otro.
 *
 * <p>Lector tolerante: si el evento trae {@code lecturasFueraDeRangoConsecutivas} se usa para
 * decidir el escalamiento; si no lo trae (versión actual del evento de sensores), cuenta como 1.</p>
 */
@Component
public class ProcesadorLecturasFueraDeRango {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorLecturasFueraDeRango.class);

    private final GenerarAlertaUseCase generarAlerta;
    private final EventoProcesadoRepository eventosProcesados;
    private final ObjectMapper objectMapper;

    public ProcesadorLecturasFueraDeRango(GenerarAlertaUseCase generarAlerta,
                                          EventoProcesadoRepository eventosProcesados,
                                          ObjectMapper objectMapper) {
        this.generarAlerta = generarAlerta;
        this.eventosProcesados = eventosProcesados;
        this.objectMapper = objectMapper;
    }

    /**
     * @param idEvento identidad estable del evento (tópico-partición-offset)
     * @return la alerta generada, o null si el evento ya se había procesado
     */
    @Transactional
    public Alerta procesar(String idEvento, String mensaje) {
        if (eventosProcesados.existsById(idEvento)) {
            log.info("Evento {} ya procesado, se ignora", idEvento);
            return null;
        }
        JsonNode evento = objectMapper.readTree(mensaje);
        GenerarAlertaComando comando = new GenerarAlertaComando(
                UUID.fromString(evento.get("envioId").asString()),
                UUID.fromString(evento.get("sensorId").asString()),
                evento.has("lecturasFueraDeRangoConsecutivas")
                        ? evento.get("lecturasFueraDeRangoConsecutivas").asInt() : 1);
        Alerta alerta = generarAlerta.generarAlerta(comando);
        eventosProcesados.save(new EventoProcesado(idEvento));
        log.info("Alerta {} ({}) generada para el envío {} desde el evento {}",
                alerta.id(), alerta.severidad().nivel(), alerta.envioId(), idEvento);
        return alerta;
    }
}
