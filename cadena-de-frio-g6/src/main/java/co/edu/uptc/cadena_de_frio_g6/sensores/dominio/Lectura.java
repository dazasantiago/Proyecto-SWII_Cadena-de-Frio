package co.edu.uptc.cadena_de_frio_g6.sensores.dominio;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Dato crudo de temperatura y humedad informado por un sensor. */
public record Lectura(UUID sensorId, UUID envioId, double temperatura, double humedad, Instant fechaHora) {

    public Lectura {
        Objects.requireNonNull(sensorId, "El id del sensor es obligatorio");
        Objects.requireNonNull(fechaHora, "La fecha y hora de la lectura son obligatorias");
        if (!Double.isFinite(temperatura) || !Double.isFinite(humedad)) {
            throw new IllegalArgumentException("La temperatura y la humedad deben ser valores finitos");
        }
    }

    public boolean tieneEnvioAsociado() {
        return envioId != null;
    }
}
