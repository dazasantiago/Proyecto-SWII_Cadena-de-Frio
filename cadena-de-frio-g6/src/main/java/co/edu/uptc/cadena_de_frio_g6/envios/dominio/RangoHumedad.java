package co.edu.uptc.cadena_de_frio_g6.envios.dominio;

import jakarta.persistence.Embeddable;

/**
 * Rango de humedad relativa, en porcentaje (0–100), que debe mantenerse durante el transporte.
 * Es un Value Object: se define solo por sus valores y es inmutable.
 */
@Embeddable
public record RangoHumedad(double minimo, double maximo) {

    public RangoHumedad {
        if (!Double.isFinite(minimo) || !Double.isFinite(maximo)) {
            throw new RangoHumedadInvalidoException(
                    "La humedad mínima (" + minimo + ") y la máxima (" + maximo + ") deben ser valores numéricos finitos");
        }
        if (minimo < 0 || maximo > 100) {
            throw new RangoHumedadInvalidoException(
                    "La humedad debe estar entre 0 y 100 (recibido: " + minimo + " a " + maximo + ")");
        }
        if (minimo >= maximo) {
            throw new RangoHumedadInvalidoException(
                    "La humedad mínima (" + minimo + ") debe ser menor que la máxima (" + maximo + ")");
        }
    }

    /** Indica si la humedad dada cumple el rango; los extremos son válidos. */
    public boolean contiene(double humedad) {
        return humedad >= minimo && humedad <= maximo;
    }
}
