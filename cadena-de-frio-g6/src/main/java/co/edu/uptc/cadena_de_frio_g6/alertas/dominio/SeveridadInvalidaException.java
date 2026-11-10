package co.edu.uptc.cadena_de_frio_g6.alertas.dominio;

public class SeveridadInvalidaException extends RuntimeException {

    public SeveridadInvalidaException(String nivel) {
        super("La severidad de la alerta debe ser NORMAL o CRITICA, no '" + nivel + "'");
    }
}
