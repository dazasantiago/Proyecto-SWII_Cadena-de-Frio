package co.edu.uptc.sensores.aplicacion;

import java.util.UUID;
import co.edu.uptc.sensores.dominio.Lectura;
import co.edu.uptc.sensores.dominio.Sensor;
import org.springframework.stereotype.Service;

/** Recibe una lectura y publica el evento fuera de rango para el servicio de alertas. */
@Service
public class IngestaLecturaService {
    private final SensorFactory sensorFactory;
    private final LecturaFactory lecturaFactory;
    private final DeteccionFueraDeRangoService deteccion;
    private final EventoLecturaPublisher publisher;

    public IngestaLecturaService(SensorFactory sensorFactory, LecturaFactory lecturaFactory,
            DeteccionFueraDeRangoService deteccion, EventoLecturaPublisher publisher) {
        this.sensorFactory = sensorFactory;
        this.lecturaFactory = lecturaFactory;
        this.deteccion = deteccion;
        this.publisher = publisher;
    }

    public DeteccionFueraDeRangoService.ResultadoLectura recibir(UUID sensorId, UUID envioId,
            double temperatura, double humedad, DeteccionFueraDeRangoService.CondicionRequerida condicion) {
        Sensor sensor = sensorFactory.crear(sensorId, envioId);
        Lectura lectura = lecturaFactory.crear(sensor, temperatura, humedad);
        var resultado = deteccion.evaluar(sensor, lectura, condicion);
        resultado.evento().ifPresent(publisher::publicar);
        return resultado;
    }
}
