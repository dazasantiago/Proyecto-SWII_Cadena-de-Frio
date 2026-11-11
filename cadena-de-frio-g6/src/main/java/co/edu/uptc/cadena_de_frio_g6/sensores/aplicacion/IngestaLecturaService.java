package co.edu.uptc.cadena_de_frio_g6.sensores.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.CondicionRequerida;
import co.edu.uptc.cadena_de_frio_g6.sensores.dominio.Lectura;
import co.edu.uptc.cadena_de_frio_g6.sensores.dominio.Sensor;
import org.springframework.stereotype.Service;

/** Recibe una medición del sensor y la pasa por el flujo de validación y detección. */
@Service
public class IngestaLecturaService {

    private final SensorFactory sensorFactory;
    private final LecturaFactory lecturaFactory;
    private final DeteccionFueraDeRangoService deteccionFueraDeRangoService;

    public IngestaLecturaService(SensorFactory sensorFactory, LecturaFactory lecturaFactory,
            DeteccionFueraDeRangoService deteccionFueraDeRangoService) {
        this.sensorFactory = sensorFactory;
        this.lecturaFactory = lecturaFactory;
        this.deteccionFueraDeRangoService = deteccionFueraDeRangoService;
    }

    /**
     * Procesa una lectura. Un envío nulo representa un sensor sin envío asociado: la lectura
     * queda descartada y no necesita condición. La condición debe ser provista por el contexto
     * de Envíos para poder comparar la medición.
     */
    public DeteccionFueraDeRangoService.ResultadoLectura recibir(
            java.util.UUID sensorId, java.util.UUID envioId, double temperatura, double humedad,
            CondicionRequerida condicion) {
        Sensor sensor = sensorFactory.crear(sensorId, envioId);
        Lectura lectura = lecturaFactory.crear(sensor, temperatura, humedad);
        return deteccionFueraDeRangoService.evaluar(sensor, lectura, condicion);
    }
}
