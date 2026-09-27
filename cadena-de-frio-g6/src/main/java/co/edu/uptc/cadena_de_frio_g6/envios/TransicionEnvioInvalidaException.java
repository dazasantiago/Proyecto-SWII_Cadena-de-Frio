package co.edu.uptc.cadena_de_frio_g6.envios;

public class TransicionEnvioInvalidaException extends RuntimeException {

    public TransicionEnvioInvalidaException(EnvioId id, EstadoEnvio actual, EstadoEnvio destino) {
        super("El envío " + id.valor() + " no puede pasar de " + actual + " a " + destino);
    }
}
