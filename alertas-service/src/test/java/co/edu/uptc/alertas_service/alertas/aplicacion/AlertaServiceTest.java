package co.edu.uptc.alertas_service.alertas.aplicacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.edu.uptc.alertas_service.alertas.dominio.Alerta;
import co.edu.uptc.alertas_service.alertas.dominio.AlertaFactory;
import co.edu.uptc.alertas_service.alertas.dominio.EscalamientoAlertaService;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Ejercita el caso de uso principal de punta a punta (generar, evaluar escalamiento,
 * persistir y consultar) sin Spring ni Mockito: solo el dominio real y un Fake del
 * puerto secundario.
 */
class AlertaServiceTest {

    private final AlertaRepository alertaRepository = new FakeAlertaRepository();
    private final AlertaService alertaService =
            new AlertaService(new AlertaFactory(), new EscalamientoAlertaService(), alertaRepository);

    @Test
    void generaYPersisteUnaAlertaNormalCuandoNoSeAlcanzaElUmbral() {
        UUID envioId = UUID.randomUUID();
        GenerarAlertaComando comando = new GenerarAlertaComando(envioId, UUID.randomUUID(), 1);

        Alerta alerta = alertaService.generarAlerta(comando);

        assertThat(alerta.esCritica()).isFalse();
        assertThat(alertaService.hayAlertaCriticaSinResolver(envioId)).isFalse();
    }

    @Test
    void escalaLaAlertaYQuedaVisibleParaOtrosSubdominiosCuandoSeAlcanzaElUmbral() {
        UUID envioId = UUID.randomUUID();
        GenerarAlertaComando comando = new GenerarAlertaComando(
                envioId, UUID.randomUUID(), EscalamientoAlertaService.LECTURAS_CONSECUTIVAS_PARA_ESCALAR);

        Alerta alerta = alertaService.generarAlerta(comando);

        assertThat(alerta.esCritica()).isTrue();
        assertThat(alertaService.hayAlertaCriticaSinResolver(envioId)).isTrue();
    }

    @Test
    void unEnvioSinAlertasNoTieneAlertasCriticasSinResolver() {
        assertThat(alertaService.hayAlertaCriticaSinResolver(UUID.randomUUID())).isFalse();
    }

    @Test
    void resolverLaAlertaCriticaDejaDeBloquearElCierreDelEnvio() {
        UUID envioId = UUID.randomUUID();
        Alerta critica = alertaService.generarAlerta(new GenerarAlertaComando(
                envioId, UUID.randomUUID(), EscalamientoAlertaService.LECTURAS_CONSECUTIVAS_PARA_ESCALAR));
        assertThat(alertaService.hayAlertaCriticaSinResolver(envioId)).isTrue();

        Alerta resuelta = alertaService.resolver(critica.id());

        assertThat(resuelta.resuelta()).isTrue();
        assertThat(alertaService.hayAlertaCriticaSinResolver(envioId)).isFalse();
    }

    @Test
    void resolverUnaAlertaQueNoExisteFalla() {
        UUID inexistente = UUID.randomUUID();

        assertThatThrownBy(() -> alertaService.resolver(inexistente))
                .isInstanceOf(AlertaNoEncontradaException.class)
                .hasMessageContaining(inexistente.toString());
    }
}
