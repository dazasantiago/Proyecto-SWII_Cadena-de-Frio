package co.edu.uptc.cadena_de_frio_g6.compartido;

import co.edu.uptc.cadena_de_frio_g6.envios.RangoInvertidoException;
import co.edu.uptc.cadena_de_frio_g6.envios.RangoSinAmplitudException;
import co.edu.uptc.cadena_de_frio_g6.envios.TemperaturaNoFinitaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduce las excepciones de dominio a respuestas HTTP. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            TemperaturaNoFinitaException.class,
            RangoInvertidoException.class,
            RangoSinAmplitudException.class
    })
    public ProblemDetail manejarRangoTemperaturaInvalido(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
