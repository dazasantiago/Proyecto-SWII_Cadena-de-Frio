package co.edu.uptc.cadena_de_frio_g6.envios.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;

import java.util.List;
import java.util.Optional;

/** Puerto secundario minimo: solo los metodos que EnvioService llama de verdad. */
public interface RepositorioEnvios {

    List<Envio> listarTodos();

    Optional<Envio> buscarPorId(EnvioId id);

    Envio guardar(Envio envio);
}
