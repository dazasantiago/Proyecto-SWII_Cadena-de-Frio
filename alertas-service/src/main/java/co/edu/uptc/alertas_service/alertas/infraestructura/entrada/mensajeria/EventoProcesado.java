package co.edu.uptc.alertas_service.alertas.infraestructura.entrada.mensajeria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** Marca de que un evento de Kafka ya generó su alerta (patrón Idempotent Consumer). */
@Entity
@Table(name = "eventos_procesados")
class EventoProcesado {

    @Id
    private String id;

    @Column(name = "procesado_en", nullable = false)
    private Instant procesadoEn;

    protected EventoProcesado() {
        // requerido por JPA
    }

    EventoProcesado(String id) {
        this.id = id;
        this.procesadoEn = Instant.now();
    }
}
