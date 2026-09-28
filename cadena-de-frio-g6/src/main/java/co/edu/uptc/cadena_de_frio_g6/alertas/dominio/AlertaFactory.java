package co.edu.uptc.cadena_de_frio_g6.alertas.dominio;

import java.time.Instant;
import java.util.UUID;

/**
 * Único punto de entrada para construir una {@link Alerta} válida.
 * Concentra la validación de negocio que antes vivía en el constructor de Alerta.
 */
public class AlertaFactory {

    public Alerta generar(UUID envioId, UUID sensorId) {
        if (envioId == null) {
            throw new EnvioRequeridoException();
        }
        if (sensorId == null) {
            throw new SensorRequeridoException();
        }

        return new Alerta(UUID.randomUUID(), envioId, sensorId, Instant.now(), SeveridadAlerta.normal());
    }
}
