package co.edu.uptc.cadena_de_frio_g6.envios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EnvioFactoryTest {

    private final EnvioFactory factory = new EnvioFactory();

    @Test
    void creaUnEnvioRegistradoConSuCondicionRequerida() {
        Envio envio = factory.crear("ENV-001", 2.0, 8.0, 60.0, 80.0);

        assertThat(envio.id()).isEqualTo(new EnvioId("ENV-001"));
        assertThat(envio.condicionRequerida()).isEqualTo(new CondicionRequerida(
                new RangoTemperatura(2.0, 8.0), new RangoHumedad(60.0, 80.0)));
        assertThat(envio.estado()).isEqualTo(EstadoEnvio.REGISTRADO);
    }

    @Test
    void noCreaUnEnvioConTemperaturaInvalida() {
        assertThatThrownBy(() -> factory.crear("ENV-001", 8.0, 2.0, 60.0, 80.0))
                .isInstanceOf(RangoInvertidoException.class);
    }

    @Test
    void noCreaUnEnvioConHumedadInvalida() {
        assertThatThrownBy(() -> factory.crear("ENV-001", 2.0, 8.0, 60.0, 120.0))
                .isInstanceOf(RangoHumedadInvalidoException.class);
    }
}
