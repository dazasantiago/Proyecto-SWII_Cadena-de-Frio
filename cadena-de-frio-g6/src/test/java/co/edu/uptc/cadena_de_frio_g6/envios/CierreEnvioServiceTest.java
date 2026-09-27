package co.edu.uptc.cadena_de_frio_g6.envios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CierreEnvioServiceTest {

    private final EnvioFactory envioFactory = new EnvioFactory();

    private ConsultaAlertasCriticas consultaAlertasCriticas;
    private CierreEnvioService service;
    private Envio envio;

    @BeforeEach
    void preparar() {
        consultaAlertasCriticas = mock(ConsultaAlertasCriticas.class);
        service = new CierreEnvioService(consultaAlertasCriticas);
        envio = envioFactory.crear("ENV-001", 2.0, 8.0, 60.0, 80.0);
        envio.iniciarTransporte();
    }

    @Test
    void cierraElEnvioCuandoNoHayAlertasCriticasSinResolver() {
        when(consultaAlertasCriticas.tieneAlertasCriticasSinResolver(envio.id())).thenReturn(false);

        service.cerrar(envio);

        assertThat(envio.estado()).isEqualTo(EstadoEnvio.CERRADO);
    }

    @Test
    void noCierraElEnvioSiHayAlertasCriticasSinResolver() {
        when(consultaAlertasCriticas.tieneAlertasCriticasSinResolver(envio.id())).thenReturn(true);

        assertThatThrownBy(() -> service.cerrar(envio))
                .isInstanceOf(EnvioConAlertasCriticasException.class)
                .hasMessageContaining("ENV-001");
        assertThat(envio.estado()).isEqualTo(EstadoEnvio.EN_TRANSITO);
    }

    @Test
    void noCierraUnEnvioQueNoEstaEnTransito() {
        Envio registrado = envioFactory.crear("ENV-002", 2.0, 8.0, 60.0, 80.0);
        when(consultaAlertasCriticas.tieneAlertasCriticasSinResolver(registrado.id())).thenReturn(false);

        assertThatThrownBy(() -> service.cerrar(registrado))
                .isInstanceOf(TransicionEnvioInvalidaException.class);
        assertThat(registrado.estado()).isEqualTo(EstadoEnvio.REGISTRADO);
    }
}
