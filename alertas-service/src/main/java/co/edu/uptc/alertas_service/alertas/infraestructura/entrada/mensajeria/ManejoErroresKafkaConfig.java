package co.edu.uptc.alertas_service.alertas.infraestructura.entrada.mensajeria;

import co.edu.uptc.alertas_service.alertas.dominio.EnvioRequeridoException;
import co.edu.uptc.alertas_service.alertas.dominio.LecturasConsecutivasNegativasException;
import co.edu.uptc.alertas_service.alertas.dominio.SensorRequeridoException;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import tools.jackson.core.JacksonException;

/**
 * Qué hacer si procesar un evento falla:
 * - un mensaje que nunca podrá procesarse (JSON roto, campo faltante, UUID inválido, regla de dominio)
 *   va directo a la Dead Letter Topic;
 * - el resto (p. ej. la base de datos no responde un momento) se reintenta con espera creciente
 *   (1 s, 2 s, 4 s) y, si sigue fallando, también va a la DLT.
 */
@Configuration
public class ManejoErroresKafkaConfig {

    @Bean
    public DefaultErrorHandler manejadorDeErrores(KafkaTemplate<String, String> kafkaTemplate) {
        // El nombre se indica explícitamente: desde Spring Kafka 4 el sufijo por defecto es "-dlt", no ".DLT".
        DeadLetterPublishingRecoverer recuperador = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (registro, error) -> new TopicPartition(registro.topic() + ".DLT", registro.partition()));

        ExponentialBackOffWithMaxRetries espera = new ExponentialBackOffWithMaxRetries(3);
        espera.setInitialInterval(1_000);
        espera.setMultiplier(2.0);
        espera.setMaxInterval(8_000);

        DefaultErrorHandler manejador = new DefaultErrorHandler(recuperador, espera);
        manejador.addNotRetryableExceptions(JacksonException.class, NullPointerException.class,
                IllegalArgumentException.class, EnvioRequeridoException.class, SensorRequeridoException.class,
                LecturasConsecutivasNegativasException.class);
        return manejador;
    }
}
