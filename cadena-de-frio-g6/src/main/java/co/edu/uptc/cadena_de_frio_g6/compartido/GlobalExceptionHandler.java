package co.edu.uptc.cadena_de_frio_g6.compartido;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import co.edu.uptc.cadena_de_frio_g6.envios.EnvioConAlertasCriticasException;
import co.edu.uptc.cadena_de_frio_g6.envios.RangoHumedadInvalidoException;
import co.edu.uptc.cadena_de_frio_g6.envios.RangoInvertidoException;
import co.edu.uptc.cadena_de_frio_g6.envios.RangoSinAmplitudException;
import co.edu.uptc.cadena_de_frio_g6.envios.TemperaturaNoFinitaException;
import co.edu.uptc.cadena_de_frio_g6.envios.TransicionEnvioInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduce las excepciones de dominio a respuestas HTTP. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TemperaturaNoFinitaException.class)
    public ResponseEntity<Map<String, Object>> manejarTemperaturaNoFinita(TemperaturaNoFinitaException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RangoInvertidoException.class)
    public ResponseEntity<Map<String, Object>> manejarRangoInvertido(RangoInvertidoException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RangoSinAmplitudException.class)
    public ResponseEntity<Map<String, Object>> manejarRangoSinAmplitud(RangoSinAmplitudException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RangoHumedadInvalidoException.class)
    public ResponseEntity<Map<String, Object>> manejarRangoHumedadInvalido(RangoHumedadInvalidoException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EnvioConAlertasCriticasException.class)
    public ResponseEntity<Map<String, Object>> manejarEnvioConAlertasCriticas(EnvioConAlertasCriticasException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(TransicionEnvioInvalidaException.class)
    public ResponseEntity<Map<String, Object>> manejarTransicionEnvioInvalida(TransicionEnvioInvalidaException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
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
