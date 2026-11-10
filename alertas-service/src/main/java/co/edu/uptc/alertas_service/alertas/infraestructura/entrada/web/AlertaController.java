package co.edu.uptc.alertas_service.alertas.infraestructura.entrada.web;

import co.edu.uptc.alertas_service.alertas.aplicacion.ConsultaAlertasCriticasUseCase;
import co.edu.uptc.alertas_service.alertas.aplicacion.GenerarAlertaComando;
import co.edu.uptc.alertas_service.alertas.aplicacion.GenerarAlertaUseCase;
import co.edu.uptc.alertas_service.alertas.aplicacion.ResolverAlertaUseCase;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador primario HTTP de Alertas. Depende solo de los puertos primarios (casos de uso),
 * nunca de AlertaService ni de JPA.
 */
@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final GenerarAlertaUseCase generarAlerta;
    private final ConsultaAlertasCriticasUseCase consultaAlertasCriticas;
    private final ResolverAlertaUseCase resolverAlerta;

    public AlertaController(GenerarAlertaUseCase generarAlerta,
                            ConsultaAlertasCriticasUseCase consultaAlertasCriticas,
                            ResolverAlertaUseCase resolverAlerta) {
        this.generarAlerta = generarAlerta;
        this.consultaAlertasCriticas = consultaAlertasCriticas;
        this.resolverAlerta = resolverAlerta;
    }

    @PostMapping
    public ResponseEntity<AlertaResponse> generar(@Valid @RequestBody GenerarAlertaRequest request) {
        var alerta = generarAlerta.generarAlerta(new GenerarAlertaComando(
                request.envioId(), request.sensorId(), request.lecturasFueraDeRangoConsecutivas()));
        return ResponseEntity.status(HttpStatus.CREATED).body(AlertaResponse.desde(alerta));
    }

    /** Lo consulta Envíos antes de cerrar un envío (CierreEnvioService). */
    @GetMapping("/envios/{envioId}/criticas-sin-resolver")
    public Map<String, Object> criticasSinResolver(@PathVariable UUID envioId) {
        return Map.of("envioId", envioId,
                "hayCriticasSinResolver", consultaAlertasCriticas.hayAlertaCriticaSinResolver(envioId));
    }

    @PatchMapping("/{alertaId}/resolver")
    public AlertaResponse resolver(@PathVariable UUID alertaId) {
        return AlertaResponse.desde(resolverAlerta.resolver(alertaId));
    }
}
