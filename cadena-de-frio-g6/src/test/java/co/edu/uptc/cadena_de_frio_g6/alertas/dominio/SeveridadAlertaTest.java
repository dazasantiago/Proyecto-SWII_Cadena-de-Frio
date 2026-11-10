package co.edu.uptc.cadena_de_frio_g6.alertas.dominio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class SeveridadAlertaTest {

    @Test
    void creaSeveridadNormal() {
        SeveridadAlerta severidad = SeveridadAlerta.normal();

        assertThat(severidad.nivel()).isEqualTo("NORMAL");
        assertThat(severidad.esCritica()).isFalse();
    }

    @Test
    void creaSeveridadCritica() {
        SeveridadAlerta severidad = SeveridadAlerta.critica();

        assertThat(severidad.nivel()).isEqualTo("CRITICA");
        assertThat(severidad.esCritica()).isTrue();
    }

    @Test
    void rechazaNivelNulo() {
        assertThatThrownBy(() -> new SeveridadAlerta(null))
                .isInstanceOf(SeveridadInvalidaException.class);
    }

    @Test
    void rechazaNivelDesconocido() {
        assertThatThrownBy(() -> new SeveridadAlerta("URGENTE"))
                .isInstanceOf(SeveridadInvalidaException.class)
                .hasMessageContaining("URGENTE");
    }

    @Test
    void dosSeveridadesConElMismoNivelSonIguales() {
        assertThat(SeveridadAlerta.normal()).isEqualTo(new SeveridadAlerta("NORMAL"));
    }
}
