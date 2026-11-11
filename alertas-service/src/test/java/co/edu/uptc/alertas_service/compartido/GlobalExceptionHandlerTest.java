package co.edu.uptc.alertas_service.compartido;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.uptc.alertas_service.alertas.aplicacion.AlertaNoEncontradaException;
import co.edu.uptc.alertas_service.alertas.dominio.EnvioRequeridoException;
import co.edu.uptc.alertas_service.alertas.dominio.LecturasConsecutivasNegativasException;
import co.edu.uptc.alertas_service.alertas.dominio.SensorRequeridoException;
import co.edu.uptc.alertas_service.alertas.dominio.SeveridadInvalidaException;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Las reglas de dominio de Alertas se traducen a 400 y una alerta inexistente a 404. */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void traduceEnvioRequeridoAUn400ConSuMensaje() {
        verificar(handler.manejarReglaDeDominio(new EnvioRequeridoException()), HttpStatus.BAD_REQUEST);
    }

    @Test
    void traduceSensorRequeridoAUn400ConSuMensaje() {
        verificar(handler.manejarReglaDeDominio(new SensorRequeridoException()), HttpStatus.BAD_REQUEST);
    }

    @Test
    void traduceSeveridadInvalidaAUn400ConSuMensaje() {
        verificar(handler.manejarReglaDeDominio(new SeveridadInvalidaException("INVALIDA")), HttpStatus.BAD_REQUEST);
    }

    @Test
    void traduceLecturasConsecutivasNegativasAUn400ConSuMensaje() {
        verificar(handler.manejarReglaDeDominio(new LecturasConsecutivasNegativasException(-1)), HttpStatus.BAD_REQUEST);
    }

    @Test
    void traduceAlertaNoEncontradaAUn404ConSuMensaje() {
        verificar(handler.manejarNoEncontrada(new AlertaNoEncontradaException(UUID.randomUUID())), HttpStatus.NOT_FOUND);
    }

    private void verificar(ResponseEntity<Map<String, Object>> respuesta, HttpStatus esperado) {
        assertThat(respuesta.getStatusCode()).isEqualTo(esperado);
        assertThat(respuesta.getBody()).containsEntry("status", esperado.value());
        assertThat(respuesta.getBody().get("mensaje")).isNotNull();
    }
}
