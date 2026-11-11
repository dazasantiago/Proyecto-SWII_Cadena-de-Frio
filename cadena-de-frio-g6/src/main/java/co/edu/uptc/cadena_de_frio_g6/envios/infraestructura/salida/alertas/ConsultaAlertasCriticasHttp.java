package co.edu.uptc.cadena_de_frio_g6.envios.infraestructura.salida.alertas;

import co.edu.uptc.cadena_de_frio_g6.envios.aplicacion.ConsultaAlertasCriticas;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Adaptador secundario de Envíos: implementa el puerto {@link ConsultaAlertasCriticas} preguntando
 * por HTTP a alertas-service, que desde la extracción de Alertas es un servicio independiente con
 * su propia base de datos. El puerto (y por tanto CierreEnvioService) no cambió: solo el adaptador.
 *
 * <p>Protegido con un Circuit Breaker: si alertas-service está caído, falla rápido en vez de dejar
 * colgada la petición de cierre.</p>
 */
@Component
public class ConsultaAlertasCriticasHttp implements ConsultaAlertasCriticas {

    private static final Logger log = LoggerFactory.getLogger(ConsultaAlertasCriticasHttp.class);

    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public ConsultaAlertasCriticasHttp(@Value("${alertas-service.url}") String urlAlertas) {
        this(RestClient.builder().requestFactory(conPlazos()), urlAlertas);
    }

    /** Recibe el builder ya preparado: lo usan las pruebas para conectarlo a un servidor simulado. */
    ConsultaAlertasCriticasHttp(RestClient.Builder builder, String urlAlertas) {
        this.restClient = builder.baseUrl(urlAlertas).build();
        this.circuitBreaker = CircuitBreaker.of("alertas-service", CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build());
        this.circuitBreaker.getEventPublisher().onStateTransition(
                e -> log.warn("Circuit Breaker alertas-service: {}", e.getStateTransition()));
    }

    @Override
    public boolean tieneAlertasCriticasSinResolver(EnvioId envioId) {
        UUID envioUuid = convertir(envioId);
        try {
            return circuitBreaker.executeSupplier(() -> consultar(envioUuid));
        } catch (CallNotPermittedException e) {
            throw new AlertasNoDisponiblesException("alertas-service no está disponible (circuito abierto)", e);
        } catch (RestClientException e) {
            throw new AlertasNoDisponiblesException("No se pudo consultar alertas-service: " + e.getMessage(), e);
        }
    }

    private boolean consultar(UUID envioUuid) {
        Map<?, ?> respuesta = restClient.get()
                .uri("/api/alertas/envios/{envioId}/criticas-sin-resolver", envioUuid)
                .retrieve()
                .body(Map.class);
        return respuesta != null && Boolean.TRUE.equals(respuesta.get("hayCriticasSinResolver"));
    }

    /** Plazos máximos de cada llamada: evita que una petición de cierre quede colgada. */
    private static SimpleClientHttpRequestFactory conPlazos() {
        SimpleClientHttpRequestFactory plazos = new SimpleClientHttpRequestFactory();
        plazos.setConnectTimeout(Duration.ofSeconds(2));
        plazos.setReadTimeout(Duration.ofSeconds(3));
        return plazos;
    }

    /** Contrato de identidad entre Envíos y Alertas: EnvioId.valor() es un UUID en texto. */
    private UUID convertir(EnvioId envioId) {
        try {
            return UUID.fromString(envioId.valor());
        } catch (IllegalArgumentException ex) {
            throw new EnvioIdNoEsUuidException(envioId);
        }
    }
}
