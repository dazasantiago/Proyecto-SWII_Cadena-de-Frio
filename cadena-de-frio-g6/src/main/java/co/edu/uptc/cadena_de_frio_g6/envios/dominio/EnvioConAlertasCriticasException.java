package co.edu.uptc.cadena_de_frio_g6.envios.dominio;

public class EnvioConAlertasCriticasException extends RuntimeException {

    public EnvioConAlertasCriticasException(EnvioId id) {
        super("El envío " + id.valor() + " no puede cerrarse: tiene alertas críticas sin resolver");
    }
}
