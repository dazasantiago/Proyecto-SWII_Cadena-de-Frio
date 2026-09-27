package co.edu.uptc.cadena_de_frio_g6.envios.dominio;

import jakarta.persistence.Embeddable;

@Embeddable
public record RangoTemperatura(double minimo, double maximo) {

    public RangoTemperatura {
        if (!Double.isFinite(minimo) || !Double.isFinite(maximo)) {
            throw new TemperaturaNoFinitaException(minimo, maximo);
        }
        if (minimo > maximo) {
            throw new RangoInvertidoException(minimo, maximo);
        }
        if (minimo == maximo) {
            throw new RangoSinAmplitudException(minimo);
        }
    }

    public boolean contiene(double temperatura) {
        return temperatura >= minimo && temperatura <= maximo;
    }
}
