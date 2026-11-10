package co.edu.uptc.cadena_de_frio_g6.envios.infraestructura.salida.alertas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/** El adaptador HTTP traduce bien las respuestas de alertas-service, sin levantar ningún servidor real. */
class ConsultaAlertasCriticasHttpTest {

    private static final String URL = "http://alertas-service";
    private static final UUID ENVIO = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String RUTA = URL + "/api/alertas/envios/" + ENVIO + "/criticas-sin-resolver";

    private MockRestServiceServer alertasService;
    private ConsultaAlertasCriticasHttp consulta;

    @BeforeEach
    void preparar() {
        RestClient.Builder builder = RestClient.builder();
        alertasService = MockRestServiceServer.bindTo(builder).build();
        consulta = new ConsultaAlertasCriticasHttp(builder, URL);
    }

    @Test
    void devuelveTrueCuandoAlertasServiceReportaCriticasSinResolver() {
        alertasService.expect(requestTo(RUTA)).andRespond(withSuccess(
                "{\"envioId\":\"" + ENVIO + "\",\"hayCriticasSinResolver\":true}", MediaType.APPLICATION_JSON));

        assertThat(consulta.tieneAlertasCriticasSinResolver(new EnvioId(ENVIO.toString()))).isTrue();
    }

    @Test
    void devuelveFalseCuandoNoHayCriticasSinResolver() {
        alertasService.expect(requestTo(RUTA)).andRespond(withSuccess(
                "{\"envioId\":\"" + ENVIO + "\",\"hayCriticasSinResolver\":false}", MediaType.APPLICATION_JSON));

        assertThat(consulta.tieneAlertasCriticasSinResolver(new EnvioId(ENVIO.toString()))).isFalse();
    }

    @Test
    void siAlertasServiceFallaNoSeAsumeNadaYSeReportaNoDisponible() {
        alertasService.expect(requestTo(RUTA)).andRespond(withServerError());

        assertThatThrownBy(() -> consulta.tieneAlertasCriticasSinResolver(new EnvioId(ENVIO.toString())))
                .isInstanceOf(AlertasNoDisponiblesException.class);
    }

    @Test
    void rechazaUnEnvioIdQueNoEsUuidSinLlamarAAlertasService() {
        assertThatThrownBy(() -> consulta.tieneAlertasCriticasSinResolver(new EnvioId("ENV-001")))
                .isInstanceOf(EnvioIdNoEsUuidException.class);
        alertasService.verify();
    }
}
