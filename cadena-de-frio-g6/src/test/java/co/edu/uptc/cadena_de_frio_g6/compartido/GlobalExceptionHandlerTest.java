package co.edu.uptc.cadena_de_frio_g6.compartido;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import co.edu.uptc.cadena_de_frio_g6.alertas.EnvioIdNoEsUuidException;
import co.edu.uptc.cadena_de_frio_g6.alertas.EnvioRequeridoException;
import co.edu.uptc.cadena_de_frio_g6.alertas.LecturasConsecutivasNegativasException;
import co.edu.uptc.cadena_de_frio_g6.alertas.SensorRequeridoException;
import co.edu.uptc.cadena_de_frio_g6.alertas.SeveridadInvalidaException;
import co.edu.uptc.cadena_de_frio_g6.envios.EnvioConAlertasCriticasException;
import co.edu.uptc.cadena_de_frio_g6.envios.EnvioId;
import co.edu.uptc.cadena_de_frio_g6.envios.EstadoEnvio;
import co.edu.uptc.cadena_de_frio_g6.envios.RangoHumedadInvalidoException;
import co.edu.uptc.cadena_de_frio_g6.envios.RangoInvertidoException;
import co.edu.uptc.cadena_de_frio_g6.envios.RangoSinAmplitudException;
import co.edu.uptc.cadena_de_frio_g6.envios.TemperaturaNoFinitaException;
import co.edu.uptc.cadena_de_frio_g6.envios.TransicionEnvioInvalidaException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void traduceTemperaturaNoFinitaAUn400ConSuMensaje() {
        TemperaturaNoFinitaException ex = new TemperaturaNoFinitaException(Double.NaN, 8.0);

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarTemperaturaNoFinita(ex);

        verificar400(respuesta, ex.getMessage());
    }

    @Test
    void traduceRangoInvertidoAUn400ConSuMensaje() {
        RangoInvertidoException ex = new RangoInvertidoException(8.0, 2.0);

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRangoInvertido(ex);

        verificar400(respuesta, ex.getMessage());
    }

    @Test
    void traduceRangoSinAmplitudAUn400ConSuMensaje() {
        RangoSinAmplitudException ex = new RangoSinAmplitudException(5.0);

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRangoSinAmplitud(ex);

        verificar400(respuesta, ex.getMessage());
    }

    @Test
    void traduceRangoHumedadInvalidoAUn400ConSuMensaje() {
        RangoHumedadInvalidoException ex = new RangoHumedadInvalidoException("humedad fuera de rango");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRangoHumedadInvalido(ex);

        verificar400(respuesta, ex.getMessage());
    }

    @Test
    void traduceEnvioRequeridoAUn400ConSuMensaje() {
        EnvioRequeridoException ex = new EnvioRequeridoException();

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarEnvioRequerido(ex);

        verificar400(respuesta, ex.getMessage());
    }

    @Test
    void traduceSensorRequeridoAUn400ConSuMensaje() {
        SensorRequeridoException ex = new SensorRequeridoException();

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarSensorRequerido(ex);

        verificar400(respuesta, ex.getMessage());
    }

    @Test
    void traduceSeveridadInvalidaAUn400ConSuMensaje() {
        SeveridadInvalidaException ex = new SeveridadInvalidaException("INVALIDA");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarSeveridadInvalida(ex);

        verificar400(respuesta, ex.getMessage());
    }

    @Test
    void traduceLecturasConsecutivasNegativasAUn400ConSuMensaje() {
        LecturasConsecutivasNegativasException ex = new LecturasConsecutivasNegativasException(-1);

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarLecturasConsecutivasNegativas(ex);

        verificar400(respuesta, ex.getMessage());
    }

    @Test
    void traduceEnvioIdNoEsUuidAUn400ConSuMensaje() {
        EnvioIdNoEsUuidException ex = new EnvioIdNoEsUuidException(new EnvioId("ENV-001"));

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarEnvioIdNoEsUuid(ex);

        verificar400(respuesta, ex.getMessage());
    }

    @Test
    void traduceEnvioConAlertasCriticasAUn409ConSuMensaje() {
        EnvioConAlertasCriticasException ex = new EnvioConAlertasCriticasException(new EnvioId("ENV-001"));

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarEnvioConAlertasCriticas(ex);

        verificar409(respuesta, ex.getMessage());
    }

    @Test
    void traduceTransicionEnvioInvalidaAUn409ConSuMensaje() {
        TransicionEnvioInvalidaException ex = new TransicionEnvioInvalidaException(
                new EnvioId("ENV-001"), EstadoEnvio.REGISTRADO, EstadoEnvio.CERRADO);

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarTransicionEnvioInvalida(ex);

        verificar409(respuesta, ex.getMessage());
    }

    private void verificar409(ResponseEntity<Map<String, Object>> respuesta, String mensaje) {
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(respuesta.getBody())
                .containsEntry("status", 409)
                .containsEntry("error", "Conflict")
                .containsEntry("mensaje", mensaje);
    }

    private void verificar400(ResponseEntity<Map<String, Object>> respuesta, String mensaje) {
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody())
                .containsEntry("status", 400)
                .containsEntry("error", "Bad Request")
                .containsEntry("mensaje", mensaje);
    }
}
