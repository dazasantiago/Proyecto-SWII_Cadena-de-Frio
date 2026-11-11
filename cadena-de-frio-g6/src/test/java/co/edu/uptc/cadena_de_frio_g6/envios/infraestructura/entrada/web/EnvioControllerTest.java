package co.edu.uptc.cadena_de_frio_g6.envios.infraestructura.entrada.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.edu.uptc.cadena_de_frio_g6.envios.aplicacion.CierreEnvioService;
import co.edu.uptc.cadena_de_frio_g6.envios.aplicacion.EnvioFactory;
import co.edu.uptc.cadena_de_frio_g6.envios.aplicacion.EnvioService;
import co.edu.uptc.cadena_de_frio_g6.envios.aplicacion.RepositorioEnvios;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioConAlertasCriticasException;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/** El adaptador web usa el caso de uso real, con un repositorio en memoria y alertas simuladas. */
class EnvioControllerTest {

    private static final String ID = "33333333-3333-3333-3333-333333333333";

    private static class RepositorioEnMemoria implements RepositorioEnvios {
        private final Map<EnvioId, Envio> envios = new HashMap<>();
        public List<Envio> listarTodos() { return new ArrayList<>(envios.values()); }
        public Optional<Envio> buscarPorId(EnvioId id) { return Optional.ofNullable(envios.get(id)); }
        public Envio guardar(Envio envio) { envios.put(envio.id(), envio); return envio; }
    }

    private boolean hayCriticasSinResolver;

    private final EnvioController controller = new EnvioController(new EnvioService(new RepositorioEnMemoria(),
            new EnvioFactory(), new CierreEnvioService(envioId -> hayCriticasSinResolver)));

    private RegistrarEnvioRequest pedido() {
        return new RegistrarEnvioRequest(ID, 2.0, 8.0, 40.0, 80.0);
    }

    @Test
    void registraUnEnvioYResponde201() {
        var respuesta = controller.registrar(pedido());

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody().estado()).isEqualTo("REGISTRADO");
        assertThat(respuesta.getBody().temperaturaMaxima()).isEqualTo(8.0);
    }

    @Test
    void cierraElEnvioCuandoAlertasNoReportaCriticas() {
        controller.registrar(pedido());
        controller.iniciarTransporte(ID);
        hayCriticasSinResolver = false;

        assertThat(controller.cerrar(ID).estado()).isEqualTo("CERRADO");
    }

    @Test
    void noCierraElEnvioSiAlertasReportaCriticasSinResolver() {
        controller.registrar(pedido());
        controller.iniciarTransporte(ID);
        hayCriticasSinResolver = true;

        assertThatThrownBy(() -> controller.cerrar(ID)).isInstanceOf(EnvioConAlertasCriticasException.class);
        assertThat(controller.buscar(ID).estado()).isEqualTo("EN_TRANSITO");
    }

    @Test
    void rechazaUnRegistroSinCondicionCompleta() {
        assertThatThrownBy(() -> controller.registrar(new RegistrarEnvioRequest(ID, 2.0, null, 40.0, 80.0)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
