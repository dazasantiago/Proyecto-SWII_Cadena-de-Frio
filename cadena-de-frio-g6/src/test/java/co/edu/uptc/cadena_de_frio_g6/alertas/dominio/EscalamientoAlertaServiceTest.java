package co.edu.uptc.cadena_de_frio_g6.alertas.dominio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EscalamientoAlertaServiceTest {

    private final EscalamientoAlertaService service = new EscalamientoAlertaService();

    private Alerta alertaNormal() {
        return new Alerta(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), Instant.now(), SeveridadAlerta.normal());
    }

    @Test
    void noEscalaSiHayMenosLecturasQueElUmbral() {
        Alerta alerta = alertaNormal();

        service.evaluar(alerta, EscalamientoAlertaService.LECTURAS_CONSECUTIVAS_PARA_ESCALAR - 1);

        assertThat(alerta.esCritica()).isFalse();
    }

    @Test
    void escalaCuandoSeAlcanzaElUmbral() {
        Alerta alerta = alertaNormal();

        service.evaluar(alerta, EscalamientoAlertaService.LECTURAS_CONSECUTIVAS_PARA_ESCALAR);

        assertThat(alerta.esCritica()).isTrue();
    }

    @Test
    void escalaCuandoSeSuperaElUmbral() {
        Alerta alerta = alertaNormal();

        service.evaluar(alerta, EscalamientoAlertaService.LECTURAS_CONSECUTIVAS_PARA_ESCALAR + 5);

        assertThat(alerta.esCritica()).isTrue();
    }

    @Test
    void rechazaUnConteoNegativo() {
        Alerta alerta = alertaNormal();

        assertThatThrownBy(() -> service.evaluar(alerta, -1))
                .isInstanceOf(LecturasConsecutivasNegativasException.class);
    }

    @Test
    void noDesescalaUnaAlertaYaResueltaAunqueSigaFueraDeRango() {
        Alerta alerta = alertaNormal();
        alerta.resolver();

        service.evaluar(alerta, EscalamientoAlertaService.LECTURAS_CONSECUTIVAS_PARA_ESCALAR);

        assertThat(alerta.esCritica()).isFalse();
    }
}
