package co.edu.uptc.cadena_de_frio_g6.envios.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;

public class EnvioNoEncontradoException extends RuntimeException {

    public EnvioNoEncontradoException(EnvioId id) {
        super("No existe un envío con id " + id.valor());
    }
}
