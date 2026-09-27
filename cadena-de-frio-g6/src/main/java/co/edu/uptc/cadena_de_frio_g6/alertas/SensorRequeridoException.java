package co.edu.uptc.cadena_de_frio_g6.alertas;

public class SensorRequeridoException extends RuntimeException {

    public SensorRequeridoException() {
        super("Una alerta debe referenciar el id del sensor que la originó");
    }
}
