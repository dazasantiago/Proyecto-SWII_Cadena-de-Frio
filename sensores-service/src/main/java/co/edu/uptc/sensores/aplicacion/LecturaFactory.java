package co.edu.uptc.sensores.aplicacion;

import java.time.Instant;
import co.edu.uptc.sensores.dominio.Lectura;
import co.edu.uptc.sensores.dominio.Sensor;
import org.springframework.stereotype.Component;

@Component
public class LecturaFactory {
    public Lectura crear(Sensor sensor, double temperatura, double humedad) {
        return new Lectura(sensor.id(), sensor.envioId(), temperatura, humedad, Instant.now());
    }
}
