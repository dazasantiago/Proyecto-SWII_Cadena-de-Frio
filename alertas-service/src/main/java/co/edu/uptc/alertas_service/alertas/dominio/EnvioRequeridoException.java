package co.edu.uptc.alertas_service.alertas.dominio;

public class EnvioRequeridoException extends RuntimeException {

    public EnvioRequeridoException() {
        super("Una alerta debe referenciar el id del envío que la originó");
    }
}
