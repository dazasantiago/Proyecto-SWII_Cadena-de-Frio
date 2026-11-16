package co.edu.uptc.sensores.api;

import java.util.UUID;
import co.edu.uptc.sensores.aplicacion.DeteccionFueraDeRangoService;
import co.edu.uptc.sensores.aplicacion.IngestaLecturaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lecturas")
public class LecturaController {
    private final IngestaLecturaService ingesta;
    public LecturaController(IngestaLecturaService ingesta) { this.ingesta = ingesta; }

    @PostMapping
    public ResponseEntity<?> recibir(@Valid @RequestBody SolicitudLectura solicitud) {
        var condicion = solicitud.condicion();
        var resultado = ingesta.recibir(solicitud.sensorId(), solicitud.envioId(), solicitud.temperatura(), solicitud.humedad(), condicion);
        return ResponseEntity.accepted().body(resultado);
    }

    public record SolicitudLectura(@NotNull UUID sensorId, UUID envioId, double temperatura, double humedad,
            Double temperaturaMinima, Double temperaturaMaxima, Double humedadMinima, Double humedadMaxima) {
        private DeteccionFueraDeRangoService.CondicionRequerida condicion() {
            if (envioId == null) return null;
            if (temperaturaMinima == null || temperaturaMaxima == null || humedadMinima == null || humedadMaxima == null) {
                throw new IllegalArgumentException("Los cuatro límites de condición son obligatorios cuando hay envío");
            }
            return new DeteccionFueraDeRangoService.CondicionRequerida(temperaturaMinima, temperaturaMaxima, humedadMinima, humedadMaxima);
        }
    }
}
