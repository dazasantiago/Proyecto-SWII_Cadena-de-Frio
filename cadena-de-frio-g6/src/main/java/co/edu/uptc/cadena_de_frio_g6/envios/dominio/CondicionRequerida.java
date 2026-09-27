package co.edu.uptc.cadena_de_frio_g6.envios.dominio;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

/**
 * Lo que debe cumplirse durante todo el transporte: el rango de temperatura y el rango de humedad
 * aceptables (HU-01). Es un Value Object; ambos componentes ya validan sus propios límites.
 */
@Embeddable
public record CondicionRequerida(
        @Embedded
        @AttributeOverrides({
                @AttributeOverride(name = "minimo", column = @Column(name = "temperatura_minima")),
                @AttributeOverride(name = "maximo", column = @Column(name = "temperatura_maxima"))
        })
        RangoTemperatura rangoTemperatura,

        @Embedded
        @AttributeOverrides({
                @AttributeOverride(name = "minimo", column = @Column(name = "humedad_minima")),
                @AttributeOverride(name = "maximo", column = @Column(name = "humedad_maxima"))
        })
        RangoHumedad rangoHumedad) {

    public CondicionRequerida {
        if (rangoTemperatura == null || rangoHumedad == null) {
            throw new IllegalArgumentException("La condición requerida necesita ambos rangos: temperatura y humedad");
        }
    }
}
