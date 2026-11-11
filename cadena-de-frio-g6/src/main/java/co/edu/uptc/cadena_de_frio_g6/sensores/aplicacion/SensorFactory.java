package co.edu.uptc.cadena_de_frio_g6.sensores.aplicacion;

import java.util.UUID;

import co.edu.uptc.cadena_de_frio_g6.sensores.dominio.Sensor;
import org.springframework.stereotype.Component;

/** Punto único de creación de sensores con identificadores asignados por el sistema. */
@Component
public class SensorFactory {

    public Sensor crear(UUID sensorId, UUID envioId) {
        if (sensorId == null) {
            throw new IllegalArgumentException("El identificador del sensor es obligatorio");
        }
        return new Sensor(sensorId, envioId);
    }
}
