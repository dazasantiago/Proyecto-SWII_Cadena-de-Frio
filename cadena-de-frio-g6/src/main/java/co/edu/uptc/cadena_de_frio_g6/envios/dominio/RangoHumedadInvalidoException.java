package co.edu.uptc.cadena_de_frio_g6.envios.dominio;

/** La humedad mínima o máxima no es un porcentaje válido (fuera de 0–100, o mínimo ≥ máximo). */
public class RangoHumedadInvalidoException extends RuntimeException {

    public RangoHumedadInvalidoException(String mensaje) {
        super(mensaje);
    }
}
