package co.edu.uptc.alertas_service.alertas.infraestructura.entrada.mensajeria;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador primario de mensajería: escucha el tópico de sensores y delega en el procesador.
 * Mantiene separado el transporte (Kafka) de la lógica de idempotencia y del caso de uso.
 */
@Component
public class LecturaFueraDeRangoListener {

    private final ProcesadorLecturasFueraDeRango procesador;

    public LecturaFueraDeRangoListener(ProcesadorLecturasFueraDeRango procesador) {
        this.procesador = procesador;
    }

    @KafkaListener(topics = TopicosConfig.LECTURAS_FUERA_DE_RANGO)
    public void alDetectarseLecturaFueraDeRango(ConsumerRecord<String, String> registro) {
        // Identidad del evento = su posición en el log de Kafka: estable ante reentregas.
        String idEvento = registro.topic() + "-" + registro.partition() + "-" + registro.offset();
        procesador.procesar(idEvento, registro.value());
    }
}
