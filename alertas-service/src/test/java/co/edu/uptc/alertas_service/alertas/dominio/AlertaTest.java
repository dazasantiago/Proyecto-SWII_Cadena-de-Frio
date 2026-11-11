package co.edu.uptc.alertas_service.alertas.dominio;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Reconstituir una alerta guardada conserva su severidad y su estado. */
class AlertaTest {

    @Test
    void reconstituirConservaSeveridadYEstadoResuelto() {
        UUID id = UUID.randomUUID();
        Alerta alerta = Alerta.reconstituir(id, UUID.randomUUID(), UUID.randomUUID(), Instant.now(),
                SeveridadAlerta.critica(), true);

        assertThat(alerta.id()).isEqualTo(id);
        assertThat(alerta.esCritica()).isTrue();
        assertThat(alerta.resuelta()).isTrue();
    }

    @Test
    void unaAlertaResueltaNoSeVuelveAEscalar() {
        Alerta alerta = Alerta.reconstituir(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                Instant.now(), SeveridadAlerta.normal(), true);

        new EscalamientoAlertaService().evaluar(alerta, EscalamientoAlertaService.LECTURAS_CONSECUTIVAS_PARA_ESCALAR);

        assertThat(alerta.esCritica()).isFalse();
    }
}
