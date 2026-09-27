package co.edu.uptc.cadena_de_frio_g6.compartido;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import co.edu.uptc.cadena_de_frio_g6.alertas.EnvioIdNoEsUuidException;
import co.edu.uptc.cadena_de_frio_g6.alertas.EnvioRequeridoException;
import co.edu.uptc.cadena_de_frio_g6.alertas.LecturasConsecutivasNegativasException;
import co.edu.uptc.cadena_de_frio_g6.alertas.SensorRequeridoException;
import co.edu.uptc.cadena_de_frio_g6.alertas.SeveridadInvalidaException;
import co.edu.uptc.cadena_de_frio_g6.envios.aplicacion.EnvioNoEncontradoException;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioConAlertasCriticasException;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EstadoEnvio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.RangoHumedadInvalidoException;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.RangoInvertidoException;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.RangoSinAmplitudException;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.TemperaturaNoFinitaException;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.TransicionEnvioInvalidaException;
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
    void traduceEnvioNoEncontradoAUn404ConSuMensaje() {
        EnvioNoEncontradoException ex = new EnvioNoEncontradoException(new EnvioId("ENV-001"));

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarEnvioNoEncontrado(ex);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(respuesta.getBody())
                .containsEntry("status", 404)
                .containsEntry("error", "Not Found")
                .containsEntry("mensaje", ex.getMessage());
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
