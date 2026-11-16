package co.edu.uptc.sensores.aplicacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;
import co.edu.uptc.sensores.dominio.LecturaFueraDeRangoDetectada;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeteccionFueraDeRangoServiceTest {
    @Mock private EventoLecturaPublisher publisher;
    @InjectMocks private IngestaLecturaService ingesta;

    private final DeteccionFueraDeRangoService.CondicionRequerida rango =
            new DeteccionFueraDeRangoService.CondicionRequerida(2, 8, 30, 70);

    @Test
    void lecturaDentroDeRangoNoPublicaEvento() {
        var resultado = ingesta.recibir(UUID.randomUUID(), UUID.randomUUID(), 5, 50, rango);
        assertThat(resultado.evento()).isEmpty();
        verifyNoInteractions(publisher);
    }

    @Test
    void publicaEventoConContadorConsecutivoYReiniciaAlVolverAlRango() {
        UUID sensorId = UUID.randomUUID();
        UUID envioId = UUID.randomUUID();
        ingesta.recibir(sensorId, envioId, 9, 50, rango);
        ingesta.recibir(sensorId, envioId, 10, 50, rango);
        var tercero = ingesta.recibir(sensorId, envioId, 11, 50, rango).evento().orElseThrow();
        assertThat(tercero.lecturasFueraDeRangoConsecutivas()).isEqualTo(3);
        verify(publisher).publicar(org.mockito.ArgumentMatchers.argThat(
                evento -> evento.lecturasFueraDeRangoConsecutivas() == 1));
        verify(publisher).publicar(org.mockito.ArgumentMatchers.argThat(
                evento -> evento.lecturasFueraDeRangoConsecutivas() == 2));
        verify(publisher).publicar(tercero);

        ingesta.recibir(sensorId, envioId, 5, 50, rango);
        LecturaFueraDeRangoDetectada reiniciada = ingesta.recibir(sensorId, envioId, 9, 50, rango).evento().orElseThrow();
        assertThat(reiniciada.lecturasFueraDeRangoConsecutivas()).isEqualTo(1);
    }

    @Test
    void lecturaSinEnvioSeDescartaSinPublicar() {
        var resultado = ingesta.recibir(UUID.randomUUID(), null, 5, 50, null);
        assertThat(resultado.descarte()).isPresent();
        verifyNoInteractions(publisher);
    }
}
