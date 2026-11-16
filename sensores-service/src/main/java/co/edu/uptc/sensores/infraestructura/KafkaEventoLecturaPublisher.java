package co.edu.uptc.sensores.infraestructura;

import co.edu.uptc.sensores.aplicacion.EventoLecturaPublisher;
import co.edu.uptc.sensores.dominio.LecturaFueraDeRangoDetectada;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventoLecturaPublisher implements EventoLecturaPublisher {
    private final KafkaTemplate<String, LecturaFueraDeRangoDetectada> kafkaTemplate;
    private final String topic;
    private final long timeoutMs;

    public KafkaEventoLecturaPublisher(KafkaTemplate<String, LecturaFueraDeRangoDetectada> kafkaTemplate,
            @Value("${sensores.kafka.topic}") String topic,
            @Value("${sensores.kafka.timeout-ms:5000}") long timeoutMs) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        if (timeoutMs <= 0) throw new IllegalArgumentException("El timeout de Kafka debe ser positivo");
        this.timeoutMs = timeoutMs;
    }

    @Override
    public void publicar(LecturaFueraDeRangoDetectada evento) {
        try {
            kafkaTemplate.send(topic, evento.envioId().toString(), evento).get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EventoPublicacionException("La publicación del evento en Kafka fue interrumpida", e);
        } catch (ExecutionException | TimeoutException | RuntimeException e) {
            throw new EventoPublicacionException("No fue posible publicar el evento en Kafka", e);
        }
    }
}
