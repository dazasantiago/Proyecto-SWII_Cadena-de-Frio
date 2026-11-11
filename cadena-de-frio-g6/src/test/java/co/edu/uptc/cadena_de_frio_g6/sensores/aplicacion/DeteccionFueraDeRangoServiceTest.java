package co.edu.uptc.cadena_de_frio_g6.sensores.aplicacion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.CondicionRequerida;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.RangoHumedad;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.RangoTemperatura;
import co.edu.uptc.cadena_de_frio_g6.sensores.dominio.Lectura;
import co.edu.uptc.cadena_de_frio_g6.sensores.dominio.Sensor;
import org.junit.jupiter.api.Test;

class DeteccionFueraDeRangoServiceTest {

    private final DeteccionFueraDeRangoService service = new DeteccionFueraDeRangoService();
    private final CondicionRequerida condicion = new CondicionRequerida(
            new RangoTemperatura(2, 8), new RangoHumedad(30, 70));

    @Test
    void lecturaDentroDeAmbosRangosNoGeneraEvento() {
        UUID envioId = UUID.randomUUID();
        Sensor sensor = new Sensor(UUID.randomUUID(), envioId);
        Lectura lectura = new Lectura(sensor.id(), envioId, 5, 50, java.time.Instant.now());

        var resultado = service.evaluar(sensor, lectura, condicion);

        assertThat(resultado.evento()).isEmpty();
        assertThat(resultado.descarte()).isEmpty();
    }

    @Test
    void temperaturaFueraDeRangoGeneraEventoConIdsDelSensorYEnvio() {
        UUID envioId = UUID.randomUUID();
        Sensor sensor = new Sensor(UUID.randomUUID(), envioId);
        Lectura lectura = new Lectura(sensor.id(), envioId, 9, 50, java.time.Instant.now());

        var resultado = service.evaluar(sensor, lectura, condicion);

        assertThat(resultado.evento()).isPresent();
        assertThat(resultado.evento().orElseThrow().envioId()).isEqualTo(envioId);
        assertThat(resultado.evento().orElseThrow().sensorId()).isEqualTo(sensor.id());
        assertThat(resultado.descarte()).isEmpty();
    }

    @Test
    void humedadFueraDeRangoGeneraEvento() {
        UUID envioId = UUID.randomUUID();
        Sensor sensor = new Sensor(UUID.randomUUID(), envioId);
        Lectura lectura = new Lectura(sensor.id(), envioId, 5, 75, java.time.Instant.now());

        assertThat(service.evaluar(sensor, lectura, condicion).evento()).isPresent();
    }

    @Test
    void lecturaSinEnvioSeDescartaSinNecesitarCondicion() {
        Sensor sensor = new Sensor(UUID.randomUUID(), null);
        Lectura lectura = new Lectura(sensor.id(), null, 5, 50, java.time.Instant.now());

        var resultado = service.evaluar(sensor, lectura, null);

        assertThat(resultado.evento()).isEmpty();
        assertThat(resultado.descarte()).isPresent();
    }
}
