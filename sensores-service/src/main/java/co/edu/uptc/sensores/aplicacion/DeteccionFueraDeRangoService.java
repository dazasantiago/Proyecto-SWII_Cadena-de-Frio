package co.edu.uptc.sensores.aplicacion;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import co.edu.uptc.sensores.dominio.Lectura;
import co.edu.uptc.sensores.dominio.LecturaDescartada;
import co.edu.uptc.sensores.dominio.LecturaFueraDeRangoDetectada;
import co.edu.uptc.sensores.dominio.Sensor;
import org.springframework.stereotype.Service;

/** Compara lecturas y lleva el conteo consecutivo por envío y sensor. */
@Service
public class DeteccionFueraDeRangoService {
    private final Map<ClaveSensor, Integer> consecutivas = new ConcurrentHashMap<>();

    public ResultadoLectura evaluar(Sensor sensor, Lectura lectura, CondicionRequerida condicion) {
        if (sensor == null || lectura == null) throw new IllegalArgumentException("El sensor y la lectura son obligatorios");
        if (!sensor.id().equals(lectura.sensorId())) throw new IllegalArgumentException("La lectura no pertenece al sensor indicado");
        if (!sensor.tieneEnvioAsociado() || !lectura.tieneEnvioAsociado() || !sensor.envioId().equals(lectura.envioId())) {
            return new ResultadoLectura(Optional.empty(), Optional.of(new LecturaDescartada(sensor.id(), "La lectura no tiene un envío asociado")));
        }
        if (condicion == null) throw new IllegalArgumentException("La condición requerida del envío es obligatoria");
        ClaveSensor clave = new ClaveSensor(sensor.envioId(), sensor.id());
        boolean fuera = !condicion.contiene(lectura.temperatura(), lectura.humedad());
        if (!fuera) {
            consecutivas.remove(clave);
            return new ResultadoLectura(Optional.empty(), Optional.empty());
        }
        int contador = consecutivas.merge(clave, 1, Integer::sum);
        var evento = new LecturaFueraDeRangoDetectada(lectura.envioId(), lectura.sensorId(), lectura.temperatura(),
                lectura.humedad(), lectura.fechaHora(), contador);
        return new ResultadoLectura(Optional.of(evento), Optional.empty());
    }

    private record ClaveSensor(UUID envioId, UUID sensorId) { }
    public record CondicionRequerida(double temperaturaMinima, double temperaturaMaxima, double humedadMinima, double humedadMaxima) {
        public CondicionRequerida {
            if (!Double.isFinite(temperaturaMinima) || !Double.isFinite(temperaturaMaxima)
                    || !Double.isFinite(humedadMinima) || !Double.isFinite(humedadMaxima)
                    || temperaturaMinima > temperaturaMaxima || humedadMinima > humedadMaxima) {
                throw new IllegalArgumentException("Los rangos de temperatura y humedad no son válidos");
            }
        }
        boolean contiene(double temperatura, double humedad) {
            return temperatura >= temperaturaMinima && temperatura <= temperaturaMaxima
                    && humedad >= humedadMinima && humedad <= humedadMaxima;
        }
    }
    public record ResultadoLectura(Optional<LecturaFueraDeRangoDetectada> evento, Optional<LecturaDescartada> descarte) { }
}
