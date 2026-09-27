package co.edu.uptc.cadena_de_frio_g6.envios.dominio;

public class RangoSinAmplitudException extends RuntimeException {

    public RangoSinAmplitudException(double temperatura) {
        super("La temperatura mínima y la máxima no pueden ser iguales (" + temperatura + ")");
    }
}
