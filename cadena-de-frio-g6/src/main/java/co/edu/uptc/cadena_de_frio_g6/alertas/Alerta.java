package co.edu.uptc.cadena_de_frio_g6.alertas;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Raíz del Agregado del Bounded Context Alertas e Incidentes.
 * Referencia Envío y Sensor únicamente por su id: nunca carga esos agregados completos.
 * Construir solo a través de {@link AlertaFactory}, dueña de la validación de negocio.
 */
public class Alerta {

    private final UUID id;
    private final UUID envioId;
    private final UUID sensorId;
    private final Instant fechaGeneracion;
    private SeveridadAlerta severidad;
    private boolean resuelta;

    Alerta(UUID id, UUID envioId, UUID sensorId, Instant fechaGeneracion, SeveridadAlerta severidad) {
        Objects.requireNonNull(id, "El id de la alerta es obligatorio");
        Objects.requireNonNull(envioId, "El id del envío es obligatorio");
        Objects.requireNonNull(sensorId, "El id del sensor es obligatorio");
        Objects.requireNonNull(fechaGeneracion, "La fecha de generación es obligatoria");
        Objects.requireNonNull(severidad, "La severidad es obligatoria");

        this.id = id;
        this.envioId = envioId;
        this.sensorId = sensorId;
        this.fechaGeneracion = fechaGeneracion;
        this.severidad = severidad;
        this.resuelta = false;
    }

    public UUID id() {
        return id;
    }

    public UUID envioId() {
        return envioId;
    }

    public UUID sensorId() {
        return sensorId;
    }

    public Instant fechaGeneracion() {
        return fechaGeneracion;
    }

    public SeveridadAlerta severidad() {
        return severidad;
    }

    public boolean esCritica() {
        return severidad.esCritica();
    }

    public boolean resuelta() {
        return resuelta;
    }

    void marcarComoCritica() {
        if (!resuelta) {
            this.severidad = SeveridadAlerta.critica();
        }
    }

    public void resolver() {
        this.resuelta = true;
    }
}
