package co.edu.uptc.cadena_de_frio_g6.alertas.infraestructura;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * Modelo de persistencia de {@code Alerta}. Vive solo en infraestructura: el dominio
 * nunca conoce esta clase ni anotaciones de JPA.
 */
@Entity
@Table(name = "alertas")
class AlertaJpaEntity {

    @Id
    private UUID id;
    private UUID envioId;
    private UUID sensorId;
    private Instant fechaGeneracion;
    private String severidad;
    private boolean resuelta;

    protected AlertaJpaEntity() {
        // requerido por JPA
    }

    AlertaJpaEntity(UUID id, UUID envioId, UUID sensorId, Instant fechaGeneracion, String severidad, boolean resuelta) {
        this.id = id;
        this.envioId = envioId;
        this.sensorId = sensorId;
        this.fechaGeneracion = fechaGeneracion;
        this.severidad = severidad;
        this.resuelta = resuelta;
    }

    UUID getId() {
        return id;
    }

    UUID getEnvioId() {
        return envioId;
    }

    UUID getSensorId() {
        return sensorId;
    }

    Instant getFechaGeneracion() {
        return fechaGeneracion;
    }

    String getSeveridad() {
        return severidad;
    }

    boolean isResuelta() {
        return resuelta;
    }
}
