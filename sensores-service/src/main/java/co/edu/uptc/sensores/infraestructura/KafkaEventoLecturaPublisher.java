package co.edu.uptc.sensores.infraestructura;

import co.edu.uptc.sensores.aplicacion.EventoLecturaPublisher;
import co.edu.uptc.sensores.dominio.LecturaFueraDeRangoDetectada;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventoLecturaPublisher implements EventoLecturaPublisher {
    private final KafkaTemplate<String, LecturaFueraDeRangoDetectada> kafkaTemplate;
    private final String topic;

    public KafkaEventoLecturaPublisher(KafkaTemplate<String, LecturaFueraDeRangoDetectada> kafkaTemplate,
            @Value("${sensores.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publicar(LecturaFueraDeRangoDetectada evento) {
        kafkaTemplate.send(topic, evento.envioId().toString(), evento);
    }
}
