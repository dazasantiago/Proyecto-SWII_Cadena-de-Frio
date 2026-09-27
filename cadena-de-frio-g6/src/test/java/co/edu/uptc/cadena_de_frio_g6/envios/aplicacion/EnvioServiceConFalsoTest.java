package co.edu.uptc.cadena_de_frio_g6.envios.aplicacion;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EstadoEnvio;
import org.junit.jupiter.api.Test;

/** Ejercita el caso de uso principal de Envios de punta a punta sin Spring ni Mockito. */
class EnvioServiceConFalsoTest {

    @Test
    void registraIniciaTransporteYCierraUnEnvioSinNingunaDependenciaDeSpringNiDeBaseDeDatos() {
        RepositorioEnviosFalso repositorio = new RepositorioEnviosFalso();
        EnvioFactory factory = new EnvioFactory();
        CierreEnvioService cierreEnvioService = new CierreEnvioService(envioId -> false);
        EnvioService service = new EnvioService(repositorio, factory, cierreEnvioService);

        Envio registrado = service.registrar("ENV-001", 2.0, 8.0, 60.0, 80.0);
        assertThat(registrado.estado()).isEqualTo(EstadoEnvio.REGISTRADO);

        service.iniciarTransporte(registrado.id());
        assertThat(service.buscarPorId(registrado.id()).estado()).isEqualTo(EstadoEnvio.EN_TRANSITO);

        service.cerrar(registrado.id());
        assertThat(service.buscarPorId(registrado.id()).estado()).isEqualTo(EstadoEnvio.CERRADO);
    }
}
