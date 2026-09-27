package co.edu.uptc.cadena_de_frio_g6.alertas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.edu.uptc.cadena_de_frio_g6.envios.EnvioId;

class ConsultaAlertasCriticasImplTest {

    private final AlertaRepository repositorio = new InMemoryAlertaRepository();
    private final AlertaFactory alertaFactory = new AlertaFactory();
    private final EscalamientoAlertaService escalamiento = new EscalamientoAlertaService();
    private final ConsultaAlertasCriticasImpl consulta = new ConsultaAlertasCriticasImpl(repositorio);

    @Test
    void noHayAlertasCriticasSinResolverCuandoElEnvioNoTieneAlertas() {
        EnvioId envioId = new EnvioId(UUID.randomUUID().toString());

        assertThat(consulta.tieneAlertasCriticasSinResolver(envioId)).isFalse();
    }

    @Test
    void detectaUnaAlertaCriticaSinResolver() {
        UUID envioUuid = UUID.randomUUID();
        Alerta alerta = alertaFactory.generar(envioUuid, UUID.randomUUID());
        escalamiento.evaluar(alerta, EscalamientoAlertaService.LECTURAS_CONSECUTIVAS_PARA_ESCALAR);
        repositorio.guardar(alerta);

        assertThat(consulta.tieneAlertasCriticasSinResolver(new EnvioId(envioUuid.toString()))).isTrue();
    }

    @Test
    void ignoraUnaAlertaCriticaYaResuelta() {
        UUID envioUuid = UUID.randomUUID();
        Alerta alerta = alertaFactory.generar(envioUuid, UUID.randomUUID());
        escalamiento.evaluar(alerta, EscalamientoAlertaService.LECTURAS_CONSECUTIVAS_PARA_ESCALAR);
        alerta.resolver();
        repositorio.guardar(alerta);

        assertThat(consulta.tieneAlertasCriticasSinResolver(new EnvioId(envioUuid.toString()))).isFalse();
    }

    @Test
    void rechazaUnEnvioIdQueNoEsUuid() {
        assertThatThrownBy(() -> consulta.tieneAlertasCriticasSinResolver(new EnvioId("ENV-001")))
                .isInstanceOf(EnvioIdNoEsUuidException.class);
    }
}
