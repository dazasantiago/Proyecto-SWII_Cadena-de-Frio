package co.edu.uptc.alertas_service.alertas.dominio;

public class SeveridadInvalidaException extends RuntimeException {

    public SeveridadInvalidaException(String nivel) {
        super("La severidad de la alerta debe ser NORMAL o CRITICA, no '" + nivel + "'");
    }
}
