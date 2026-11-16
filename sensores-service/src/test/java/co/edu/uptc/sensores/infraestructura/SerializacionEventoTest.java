package co.edu.uptc.sensores.infraestructura;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.uptc.sensores.dominio.LecturaFueraDeRangoDetectada;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.jupiter.api.Test;

/**
 * Usa el serializador que Kafka instancia a partir de application.properties y comprueba que el
 * JSON trae los campos que lee alertas-service (ProcesadorLecturasFueraDeRango).
 */
class SerializacionEventoTest {

    @Test
    @SuppressWarnings("unchecked")
    void elSerializadorConfiguradoProduceElJsonQueEsperaAlertas() throws Exception {
        Properties propiedades = new Properties();
        try (InputStream entrada = getClass().getResourceAsStream("/application.properties")) {
            propiedades.load(entrada);
        }
        String claseSerializador = propiedades.getProperty("spring.kafka.producer.value-serializer");
        var serializador = (Serializer<LecturaFueraDeRangoDetectada>) Class.forName(claseSerializador)
                .getDeclaredConstructor().newInstance();

        var evento = new LecturaFueraDeRangoDetectada(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                9.5, 70.0, Instant.parse("2026-11-10T10:00:00Z"), 3);

        String json = new String(serializador.serialize("sensores.lecturas-fuera-de-rango", evento),
                StandardCharsets.UTF_8);

        assertThat(json)
                .contains("\"envioId\":\"11111111-1111-1111-1111-111111111111\"")
                .contains("\"sensorId\":\"22222222-2222-2222-2222-222222222222\"")
                .contains("\"lecturasFueraDeRangoConsecutivas\":3");
    }
}
