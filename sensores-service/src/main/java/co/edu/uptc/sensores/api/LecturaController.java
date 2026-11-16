package co.edu.uptc.sensores.api;

import java.util.UUID;
import co.edu.uptc.sensores.aplicacion.DeteccionFueraDeRangoService;
import co.edu.uptc.sensores.aplicacion.IngestaLecturaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
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

    public record SolicitudLectura(@NotNull UUID sensorId, UUID envioId, @NotNull Double temperatura, @NotNull Double humedad,
            Double temperaturaMinima, Double temperaturaMaxima, Double humedadMinima, Double humedadMaxima) {
        @AssertTrue(message = "Los cuatro límites deben ser finitos y formar rangos válidos cuando hay envío")
        public boolean isCondicionValida() {
            if (envioId == null) return true;
            return temperaturaMinima != null && temperaturaMaxima != null && humedadMinima != null && humedadMaxima != null
                    && Double.isFinite(temperaturaMinima) && Double.isFinite(temperaturaMaxima)
                    && Double.isFinite(humedadMinima) && Double.isFinite(humedadMaxima)
                    && temperaturaMinima <= temperaturaMaxima && humedadMinima <= humedadMaxima;
        }

        private DeteccionFueraDeRangoService.CondicionRequerida condicion() {
            if (envioId == null) return null;
            return new DeteccionFueraDeRangoService.CondicionRequerida(temperaturaMinima, temperaturaMaxima, humedadMinima, humedadMaxima);
        }
    }
}
