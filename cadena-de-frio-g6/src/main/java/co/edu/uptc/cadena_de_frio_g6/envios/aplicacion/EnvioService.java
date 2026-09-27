package co.edu.uptc.cadena_de_frio_g6.envios.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;
import org.springframework.stereotype.Service;

import java.util.List;

/** Orquesta la Factory, el repositorio y el Servicio de Dominio de cierre. */
@Service
public class EnvioService implements EnvioUseCase {

    private final RepositorioEnvios repositorioEnvios;
    private final EnvioFactory envioFactory;
    private final CierreEnvioService cierreEnvioService;

    public EnvioService(RepositorioEnvios repositorioEnvios, EnvioFactory envioFactory,
            CierreEnvioService cierreEnvioService) {
        this.repositorioEnvios = repositorioEnvios;
        this.envioFactory = envioFactory;
        this.cierreEnvioService = cierreEnvioService;
    }

    @Override
    public List<Envio> listarTodos() {
        return repositorioEnvios.listarTodos();
    }

    @Override
    public Envio buscarPorId(EnvioId id) {
        return repositorioEnvios.buscarPorId(id)
                .orElseThrow(() -> new EnvioNoEncontradoException(id));
    }

    @Override
    public Envio registrar(String id, double temperaturaMinima, double temperaturaMaxima,
            double humedadMinima, double humedadMaxima) {
        Envio envio = envioFactory.crear(id, temperaturaMinima, temperaturaMaxima, humedadMinima, humedadMaxima);
        return repositorioEnvios.guardar(envio);
    }

    @Override
    public void iniciarTransporte(EnvioId id) {
        Envio envio = buscarPorId(id);
        envio.iniciarTransporte();
        repositorioEnvios.guardar(envio);
    }

    @Override
    public void cerrar(EnvioId id) {
        Envio envio = buscarPorId(id);
        cierreEnvioService.cerrar(envio);
        repositorioEnvios.guardar(envio);
    }
}
