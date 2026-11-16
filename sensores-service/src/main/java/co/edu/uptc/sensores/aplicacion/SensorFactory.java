package co.edu.uptc.sensores.aplicacion;

import java.util.UUID;
import co.edu.uptc.sensores.dominio.Sensor;
import org.springframework.stereotype.Component;

@Component
public class SensorFactory {
    public Sensor crear(UUID sensorId, UUID envioId) { return new Sensor(sensorId, envioId); }
}
