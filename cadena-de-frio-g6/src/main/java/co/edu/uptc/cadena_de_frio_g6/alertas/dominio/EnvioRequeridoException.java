package co.edu.uptc.cadena_de_frio_g6.alertas.dominio;

public class EnvioRequeridoException extends RuntimeException {

    public EnvioRequeridoException() {
        super("Una alerta debe referenciar el id del envío que la originó");
    }
}
