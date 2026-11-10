package co.edu.uptc.cadena_de_frio_g6.alertas.infraestructura;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.edu.uptc.cadena_de_frio_g6.alertas.aplicacion.AlertaRepository;
import co.edu.uptc.cadena_de_frio_g6.alertas.aplicacion.AlertaService;
import co.edu.uptc.cadena_de_frio_g6.alertas.dominio.Alerta;
import co.edu.uptc.cadena_de_frio_g6.alertas.dominio.AlertaFactory;
import co.edu.uptc.cadena_de_frio_g6.alertas.dominio.EscalamientoAlertaService;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;
import java.util.HashMap;
import java.util.Map;

class ConsultaAlertasCriticasImplTest {

    /** Repositorio en memoria solo para la prueba (el de producción es el adaptador JPA). */
    private static class RepositorioEnMemoria implements AlertaRepository {
        private final Map<UUID, Alerta> alertas = new HashMap<>();

        @Override
        public void guardar(Alerta alerta) {
            alertas.put(alerta.id(), alerta);
        }

        @Override
        public boolean existeAlertaCriticaSinResolver(UUID envioId) {
            return alertas.values().stream()
                    .anyMatch(a -> a.envioId().equals(envioId) && a.esCritica() && !a.resuelta());
        }
    }

    private final AlertaRepository repositorio = new RepositorioEnMemoria();
    private final AlertaFactory alertaFactory = new AlertaFactory();
    private final EscalamientoAlertaService escalamiento = new EscalamientoAlertaService();
    private final ConsultaAlertasCriticasImpl consulta = new ConsultaAlertasCriticasImpl(
            new AlertaService(alertaFactory, escalamiento, repositorio));

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
