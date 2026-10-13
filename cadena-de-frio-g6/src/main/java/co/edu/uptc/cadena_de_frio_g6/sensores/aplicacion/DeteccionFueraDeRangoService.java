package co.edu.uptc.cadena_de_frio_g6.sensores.aplicacion;

import java.util.Optional;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.CondicionRequerida;
import co.edu.uptc.cadena_de_frio_g6.sensores.dominio.Lectura;
import co.edu.uptc.cadena_de_frio_g6.sensores.dominio.LecturaDescartada;
import co.edu.uptc.cadena_de_frio_g6.sensores.dominio.LecturaFueraDeRangoDetectada;
import co.edu.uptc.cadena_de_frio_g6.sensores.dominio.Sensor;
import org.springframework.stereotype.Service;

/** Valida la asociación de la lectura y la compara con la condición requerida del envío. */
@Service
public class DeteccionFueraDeRangoService {

    public ResultadoLectura evaluar(Sensor sensor, Lectura lectura, CondicionRequerida condicion) {
        if (sensor == null || lectura == null) {
            throw new IllegalArgumentException("El sensor y la lectura son obligatorios");
        }
        if (!sensor.id().equals(lectura.sensorId())) {
            throw new IllegalArgumentException("La lectura no pertenece al sensor indicado");
        }
        if (!sensor.tieneEnvioAsociado() || !lectura.tieneEnvioAsociado()
                || !sensor.envioId().equals(lectura.envioId())) {
            return new ResultadoLectura(Optional.empty(),
                    Optional.of(new LecturaDescartada(sensor.id(), "La lectura no tiene un envío asociado")));
        }
        if (condicion == null) {
            throw new IllegalArgumentException("La condición requerida del envío es obligatoria");
        }

        boolean fueraDeRango = !condicion.rangoTemperatura().contiene(lectura.temperatura())
                || !condicion.rangoHumedad().contiene(lectura.humedad());
        Optional<LecturaFueraDeRangoDetectada> evento = fueraDeRango
                ? Optional.of(new LecturaFueraDeRangoDetectada(lectura.envioId(), lectura.sensorId(),
                        lectura.temperatura(), lectura.humedad(), lectura.fechaHora()))
                : Optional.empty();
        return new ResultadoLectura(evento, Optional.empty());
    }

    public record ResultadoLectura(Optional<LecturaFueraDeRangoDetectada> evento,
            Optional<LecturaDescartada> descarte) {
    }
}
