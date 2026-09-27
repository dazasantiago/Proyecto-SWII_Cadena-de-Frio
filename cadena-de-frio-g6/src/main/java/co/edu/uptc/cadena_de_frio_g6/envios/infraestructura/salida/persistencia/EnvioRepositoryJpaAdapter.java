package co.edu.uptc.cadena_de_frio_g6.envios.infraestructura.salida.persistencia;

import co.edu.uptc.cadena_de_frio_g6.envios.aplicacion.RepositorioEnvios;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EnvioRepositoryJpaAdapter implements RepositorioEnvios {

    private final EnvioJpaRepository envioJpaRepository;

    public EnvioRepositoryJpaAdapter(EnvioJpaRepository envioJpaRepository) {
        this.envioJpaRepository = envioJpaRepository;
    }

    @Override
    public List<Envio> listarTodos() {
        return envioJpaRepository.findAll();
    }

    @Override
    public Optional<Envio> buscarPorId(EnvioId id) {
        return envioJpaRepository.findById(id);
    }

    @Override
    public Envio guardar(Envio envio) {
        return envioJpaRepository.save(envio);
    }
}
