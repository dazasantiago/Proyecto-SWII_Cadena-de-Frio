package co.edu.uptc.cadena_de_frio_g6.envios.dominio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RangoHumedadTest {

    @Test
    void creaRangoValido() {
        RangoHumedad rango = new RangoHumedad(60.0, 80.0);

        assertThat(rango.contiene(70.0)).isTrue();
    }

    @Test
    void rechazaValoresFueraDeCeroACien() {
        assertThatThrownBy(() -> new RangoHumedad(60.0, 120.0))
                .isInstanceOf(RangoHumedadInvalidoException.class);
    }

    @Test
    void rechazaMinimoMayorOIgualQueMaximo() {
        assertThatThrownBy(() -> new RangoHumedad(80.0, 60.0))
                .isInstanceOf(RangoHumedadInvalidoException.class);
    }
}
