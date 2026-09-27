package co.edu.uptc.cadena_de_frio_g6.envios;

/**
 * Lo que debe cumplirse durante todo el transporte: el rango de temperatura y el rango de humedad
 * aceptables (HU-01). Es un Value Object; ambos componentes ya validan sus propios límites.
 */
public record CondicionRequerida(RangoTemperatura rangoTemperatura, RangoHumedad rangoHumedad) {

    public CondicionRequerida {
        if (rangoTemperatura == null || rangoHumedad == null) {
            throw new IllegalArgumentException("La condición requerida necesita ambos rangos: temperatura y humedad");
        }
    }
}
