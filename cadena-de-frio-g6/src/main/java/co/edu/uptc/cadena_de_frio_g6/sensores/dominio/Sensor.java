package co.edu.uptc.cadena_de_frio_g6.sensores.dominio;

import java.util.Objects;
import java.util.UUID;

/** Dispositivo que reporta lecturas para un envío. */
public record Sensor(UUID id, UUID envioId) {

    public Sensor {
        Objects.requireNonNull(id, "El id del sensor es obligatorio");
    }

    public boolean tieneEnvioAsociado() {
        return envioId != null;
    }
}
