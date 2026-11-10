package co.edu.uptc.alertas_service.compartido;

import co.edu.uptc.alertas_service.alertas.aplicacion.AlertaNoEncontradaException;
import co.edu.uptc.alertas_service.alertas.dominio.EnvioRequeridoException;
import co.edu.uptc.alertas_service.alertas.dominio.LecturasConsecutivasNegativasException;
import co.edu.uptc.alertas_service.alertas.dominio.SensorRequeridoException;
import co.edu.uptc.alertas_service.alertas.dominio.SeveridadInvalidaException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las excepciones de Alertas a respuestas HTTP, con el mismo formato que usa el
 * GlobalExceptionHandler de cadena-de-frio-g6 (timestamp, status, error, mensaje).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EnvioRequeridoException.class, SensorRequeridoException.class,
            SeveridadInvalidaException.class, LecturasConsecutivasNegativasException.class})
    public ResponseEntity<Map<String, Object>> manejarReglaDeDominio(RuntimeException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        String campo = ex.getBindingResult().getFieldErrors().isEmpty() ? "cuerpo"
                : ex.getBindingResult().getFieldErrors().get(0).getField();
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Dato inválido o faltante: " + campo);
    }

    @ExceptionHandler(AlertaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrada(AlertaNoEncontradaException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus estado, String mensaje) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", Instant.now().toString());
        cuerpo.put("status", estado.value());
        cuerpo.put("error", estado.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
