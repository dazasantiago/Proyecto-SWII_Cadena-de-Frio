package co.edu.uptc.cadena_de_frio_g6.envios.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;

import java.util.List;

/** Puerto primario: lo que alguien externo (un futuro controlador, un test) puede pedirle a Envios. */
public interface EnvioUseCase {

    List<Envio> listarTodos();

    Envio buscarPorId(EnvioId id);

    Envio registrar(String id, double temperaturaMinima, double temperaturaMaxima,
            double humedadMinima, double humedadMaxima);

    void iniciarTransporte(EnvioId id);

    void cerrar(EnvioId id);
}
