package co.edu.uptc.sensores.infraestructura;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import co.edu.uptc.sensores.dominio.LecturaFueraDeRangoDetectada;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KafkaEventoLecturaPublisherTest {
    private static final String TOPIC = "sensores.lecturas-fuera-de-rango";
    @Mock private KafkaTemplate<String, LecturaFueraDeRangoDetectada> kafkaTemplate;

    @Test
    void publicaConTopicoYClaveDelEnvio() {
        UUID envioId = UUID.randomUUID();
        var evento = new LecturaFueraDeRangoDetectada(envioId, UUID.randomUUID(), 9, 50, Instant.now(), 3);
        when(kafkaTemplate.send(eq(TOPIC), eq(envioId.toString()), eq(evento)))
                .thenReturn(CompletableFuture.completedFuture(null));

        new KafkaEventoLecturaPublisher(kafkaTemplate, TOPIC, 100).publicar(evento);

        verify(kafkaTemplate).send(TOPIC, envioId.toString(), evento);
    }

    @Test
    void informaFalloAsincronoDeKafka() {
        UUID envioId = UUID.randomUUID();
        var evento = new LecturaFueraDeRangoDetectada(envioId, UUID.randomUUID(), 9, 50, Instant.now(), 3);
        var resultado = new CompletableFuture<SendResult<String, LecturaFueraDeRangoDetectada>>();
        resultado.completeExceptionally(new IllegalStateException("broker desconectado"));
        when(kafkaTemplate.send(eq(TOPIC), eq(envioId.toString()), eq(evento))).thenReturn(resultado);

        assertThatThrownBy(() -> new KafkaEventoLecturaPublisher(kafkaTemplate, TOPIC, 100).publicar(evento))
                .isInstanceOf(EventoPublicacionException.class)
                .hasMessageContaining("No fue posible publicar");
    }

    @Test
    void informaTimeoutDeKafka() {
        UUID envioId = UUID.randomUUID();
        var evento = new LecturaFueraDeRangoDetectada(envioId, UUID.randomUUID(), 9, 50, Instant.now(), 3);
        when(kafkaTemplate.send(eq(TOPIC), eq(envioId.toString()), eq(evento))).thenReturn(new CompletableFuture<>());

        assertThatThrownBy(() -> new KafkaEventoLecturaPublisher(kafkaTemplate, TOPIC, 1).publicar(evento))
                .isInstanceOf(EventoPublicacionException.class)
                .hasMessageContaining("No fue posible publicar");
    }
}
