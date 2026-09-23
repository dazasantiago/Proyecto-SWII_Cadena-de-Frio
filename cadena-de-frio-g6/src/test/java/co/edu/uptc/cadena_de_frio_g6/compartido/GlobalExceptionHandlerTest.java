package co.edu.uptc.cadena_de_frio_g6.compartido;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.uptc.cadena_de_frio_g6.envios.RangoInvertidoException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void traduceUnRangoInvalidoAUn400ConSuMensaje() {
        RangoInvertidoException ex = new RangoInvertidoException(8.0, 2.0);

        ProblemDetail respuesta = handler.manejarRangoTemperaturaInvalido(ex);

        assertThat(respuesta.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(respuesta.getDetail()).isEqualTo(ex.getMessage());
    }
}
