package co.edu.uptc.cadena_de_frio_g6.envios.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;

public class EnvioYaRegistradoException extends RuntimeException {

    public EnvioYaRegistradoException(EnvioId id) {
        super("Ya existe un envío con id " + id.valor());
    }
}
