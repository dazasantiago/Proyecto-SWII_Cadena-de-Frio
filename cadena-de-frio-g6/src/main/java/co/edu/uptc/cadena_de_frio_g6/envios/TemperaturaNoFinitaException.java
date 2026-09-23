package co.edu.uptc.cadena_de_frio_g6.envios;

public class TemperaturaNoFinitaException extends RuntimeException {

    public TemperaturaNoFinitaException(double minimo, double maximo) {
        super("La temperatura mínima (" + minimo + ") y la máxima (" + maximo + ") deben ser valores numéricos finitos");
    }
}
