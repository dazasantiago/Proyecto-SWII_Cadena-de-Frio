package co.edu.uptc.cadena_de_frio_g6.envios;

public class RangoInvertidoException extends RuntimeException {

    public RangoInvertidoException(double minimo, double maximo) {
        super("La temperatura mínima (" + minimo + ") no puede ser mayor que la máxima (" + maximo + ")");
    }
}
