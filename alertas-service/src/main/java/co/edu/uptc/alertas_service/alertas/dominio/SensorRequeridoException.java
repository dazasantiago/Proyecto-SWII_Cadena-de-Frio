package co.edu.uptc.alertas_service.alertas.dominio;

public class SensorRequeridoException extends RuntimeException {

    public SensorRequeridoException() {
        super("Una alerta debe referenciar el id del sensor que la originó");
    }
}
