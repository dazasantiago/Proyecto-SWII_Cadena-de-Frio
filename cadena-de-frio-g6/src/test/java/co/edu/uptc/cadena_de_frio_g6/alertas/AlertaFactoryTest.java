package co.edu.uptc.cadena_de_frio_g6.alertas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AlertaFactoryTest {

    private final AlertaFactory factory = new AlertaFactory();

    @Test
    void generaUnaAlertaNormalConIdYFechaAsignados() {
        UUID envioId = UUID.randomUUID();
        UUID sensorId = UUID.randomUUID();

        Alerta alerta = factory.generar(envioId, sensorId);

        assertThat(alerta.id()).isNotNull();
        assertThat(alerta.envioId()).isEqualTo(envioId);
        assertThat(alerta.sensorId()).isEqualTo(sensorId);
        assertThat(alerta.fechaGeneracion()).isNotNull();
        assertThat(alerta.esCritica()).isFalse();
        assertThat(alerta.resuelta()).isFalse();
    }

    @Test
    void dosAlertasGeneradasTienenIdsDistintos() {
        UUID envioId = UUID.randomUUID();
        UUID sensorId = UUID.randomUUID();

        Alerta primera = factory.generar(envioId, sensorId);
        Alerta segunda = factory.generar(envioId, sensorId);

        assertThat(primera.id()).isNotEqualTo(segunda.id());
    }

    @Test
    void rechazaEnvioIdNulo() {
        assertThatThrownBy(() -> factory.generar(null, UUID.randomUUID()))
                .isInstanceOf(EnvioRequeridoException.class);
    }

    @Test
    void rechazaSensorIdNulo() {
        assertThatThrownBy(() -> factory.generar(UUID.randomUUID(), null))
                .isInstanceOf(SensorRequeridoException.class);
    }
}
