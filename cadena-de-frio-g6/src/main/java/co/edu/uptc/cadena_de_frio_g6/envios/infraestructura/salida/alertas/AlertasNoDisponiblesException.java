package co.edu.uptc.cadena_de_frio_g6.envios.infraestructura.salida.alertas;

/**
 * No se pudo preguntar a alertas-service si el envío tiene alertas críticas sin resolver.
 * Es distinto de "sí tiene": no se sabe, así que por seguridad no se cierra el envío (503).
 */
public class AlertasNoDisponiblesException extends RuntimeException {

    public AlertasNoDisponiblesException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
