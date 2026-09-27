package co.edu.uptc.cadena_de_frio_g6.envios.dominio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RangoTemperaturaTest {

    @Test
    void creaRangoValido() {
        RangoTemperatura rango = new RangoTemperatura(2.0, 8.0);

        assertThat(rango.minimo()).isEqualTo(2.0);
        assertThat(rango.maximo()).isEqualTo(8.0);
    }

    @Test
    void aceptaTemperaturasBajoCero() {
        RangoTemperatura rango = new RangoTemperatura(-25.0, -18.0);

        assertThat(rango.contiene(-20.0)).isTrue();
    }

    @Test
    void rechazaMinimoMayorQueMaximo() {
        assertThatThrownBy(() -> new RangoTemperatura(8.0, 2.0))
                .isInstanceOf(RangoInvertidoException.class)
                .hasMessageContaining("8.0")
                .hasMessageContaining("2.0");
    }

    @Test
    void rechazaMinimoIgualAlMaximo() {
        assertThatThrownBy(() -> new RangoTemperatura(5.0, 5.0))
                .isInstanceOf(RangoSinAmplitudException.class);
    }

    @Test
    void rechazaValoresNoFinitos() {
        assertThatThrownBy(() -> new RangoTemperatura(Double.NaN, 8.0))
                .isInstanceOf(TemperaturaNoFinitaException.class);
        assertThatThrownBy(() -> new RangoTemperatura(2.0, Double.POSITIVE_INFINITY))
                .isInstanceOf(TemperaturaNoFinitaException.class);
    }

    @Test
    void contieneIncluyeLosExtremos() {
        RangoTemperatura rango = new RangoTemperatura(2.0, 8.0);

        assertThat(rango.contiene(2.0)).isTrue();
        assertThat(rango.contiene(8.0)).isTrue();
        assertThat(rango.contiene(5.0)).isTrue();
    }

    @Test
    void contieneRechazaFueraDeRango() {
        RangoTemperatura rango = new RangoTemperatura(2.0, 8.0);

        assertThat(rango.contiene(1.9)).isFalse();
        assertThat(rango.contiene(8.1)).isFalse();
    }

    @Test
    void dosRangosConLosMismosValoresSonIguales() {
        assertThat(new RangoTemperatura(2.0, 8.0)).isEqualTo(new RangoTemperatura(2.0, 8.0));
    }
}
