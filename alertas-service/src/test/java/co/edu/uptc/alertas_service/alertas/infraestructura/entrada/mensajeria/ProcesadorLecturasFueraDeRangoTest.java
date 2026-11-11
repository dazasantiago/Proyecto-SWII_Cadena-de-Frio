package co.edu.uptc.alertas_service.alertas.infraestructura.entrada.mensajeria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.uptc.alertas_service.alertas.aplicacion.GenerarAlertaComando;
import co.edu.uptc.alertas_service.alertas.aplicacion.GenerarAlertaUseCase;
import co.edu.uptc.alertas_service.alertas.dominio.Alerta;
import co.edu.uptc.alertas_service.alertas.dominio.AlertaFactory;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/** El consumidor traduce el evento de sensores en una alerta, una sola vez por evento. */
class ProcesadorLecturasFueraDeRangoTest {

    private static final UUID ENVIO = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SENSOR = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final String ID_EVENTO = "sensores.lecturas-fuera-de-rango-0-7";

    private GenerarAlertaUseCase generarAlerta;
    private EventoProcesadoRepository eventosProcesados;
    private ProcesadorLecturasFueraDeRango procesador;

    @BeforeEach
    void preparar() {
        generarAlerta = mock(GenerarAlertaUseCase.class);
        eventosProcesados = mock(EventoProcesadoRepository.class);
        Alerta alerta = new AlertaFactory().generar(ENVIO, SENSOR);
        when(generarAlerta.generarAlerta(any())).thenReturn(alerta);
        procesador = new ProcesadorLecturasFueraDeRango(generarAlerta, eventosProcesados, JsonMapper.builder().build());
    }

    /** Evento tal como lo publica hoy Lecturas de Sensores (sin el conteo de lecturas seguidas). */
    private static String eventoDeSensores() {
        return "{\"envioId\":\"" + ENVIO + "\",\"sensorId\":\"" + SENSOR
                + "\",\"temperatura\":9.5,\"humedad\":70.0,\"fechaHora\":\"2026-11-10T10:00:00Z\"}";
    }

    @Test
    void sinElConteoDeLecturasSeguidasGeneraUnaAlertaContandoUna() {
        procesador.procesar(ID_EVENTO, eventoDeSensores());

        ArgumentCaptor<GenerarAlertaComando> comando = ArgumentCaptor.forClass(GenerarAlertaComando.class);
        verify(generarAlerta).generarAlerta(comando.capture());
        assertThat(comando.getValue()).isEqualTo(new GenerarAlertaComando(ENVIO, SENSOR, 1));
        verify(eventosProcesados).save(any(EventoProcesado.class));
    }

    @Test
    void usaElConteoDeLecturasSeguidasCuandoElEventoLoTrae() {
        String conConteo = eventoDeSensores().replace("}", ",\"lecturasFueraDeRangoConsecutivas\":3}");

        procesador.procesar(ID_EVENTO, conConteo);

        ArgumentCaptor<GenerarAlertaComando> comando = ArgumentCaptor.forClass(GenerarAlertaComando.class);
        verify(generarAlerta).generarAlerta(comando.capture());
        assertThat(comando.getValue().lecturasFueraDeRangoConsecutivas()).isEqualTo(3);
    }

    @Test
    void unEventoYaProcesadoNoGeneraOtraAlerta() {
        when(eventosProcesados.existsById(ID_EVENTO)).thenReturn(true);

        Alerta resultado = procesador.procesar(ID_EVENTO, eventoDeSensores());

        assertThat(resultado).isNull();
        verify(generarAlerta, never()).generarAlerta(any());
        verify(eventosProcesados, never()).save(any());
    }

    @Test
    void unMensajeQueNoEsJsonFallaSinGenerarAlerta() {
        assertThatThrownBy(() -> procesador.procesar(ID_EVENTO, "esto no es json"))
                .isInstanceOf(JacksonException.class);
        verify(generarAlerta, never()).generarAlerta(any());
        verify(eventosProcesados, never()).save(any());
    }

    @Test
    void unEnvioIdQueNoEsUuidFallaSinGenerarAlerta() {
        String malo = eventoDeSensores().replace(ENVIO.toString(), "ENV-001");

        assertThatThrownBy(() -> procesador.procesar(ID_EVENTO, malo))
                .isInstanceOf(IllegalArgumentException.class);
        verify(generarAlerta, never()).generarAlerta(any());
    }
}
