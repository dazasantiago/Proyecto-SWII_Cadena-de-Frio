package co.edu.uptc.cadena_de_frio_g6.sensores.aplicacion;

import java.time.Instant;

import co.edu.uptc.cadena_de_frio_g6.sensores.dominio.Lectura;
import co.edu.uptc.cadena_de_frio_g6.sensores.dominio.Sensor;
import org.springframework.stereotype.Component;

/** Construye lecturas con la identidad del sensor y la hora de recepción. */
@Component
public class LecturaFactory {

    public Lectura crear(Sensor sensor, double temperatura, double humedad) {
        if (sensor == null) {
            throw new IllegalArgumentException("El sensor es obligatorio para registrar una lectura");
        }
        return new Lectura(sensor.id(), sensor.envioId(), temperatura, humedad, Instant.now());
    }
}
