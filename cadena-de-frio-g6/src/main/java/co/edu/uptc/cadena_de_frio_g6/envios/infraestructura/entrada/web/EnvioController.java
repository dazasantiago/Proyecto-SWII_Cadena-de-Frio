package co.edu.uptc.cadena_de_frio_g6.envios.infraestructura.entrada.web;

import co.edu.uptc.cadena_de_frio_g6.envios.aplicacion.EnvioUseCase;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador primario HTTP de Envíos. Solo traduce HTTP/JSON hacia el puerto EnvioUseCase: la
 * regla de cierre sigue en CierreEnvioService, que pregunta a alertas-service.
 */
@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final EnvioUseCase envios;

    public EnvioController(EnvioUseCase envios) {
        this.envios = envios;
    }

    @PostMapping
    public ResponseEntity<EnvioResponse> registrar(@RequestBody RegistrarEnvioRequest request) {
        request.validar();
        var envio = envios.registrar(request.id(), request.temperaturaMinima(), request.temperaturaMaxima(),
                request.humedadMinima(), request.humedadMaxima());
        return ResponseEntity.status(HttpStatus.CREATED).body(EnvioResponse.desde(envio));
    }

    @GetMapping
    public List<EnvioResponse> listar() {
        return envios.listarTodos().stream().map(EnvioResponse::desde).toList();
    }

    @GetMapping("/{id}")
    public EnvioResponse buscar(@PathVariable String id) {
        return EnvioResponse.desde(envios.buscarPorId(new EnvioId(id)));
    }

    @PostMapping("/{id}/iniciar-transporte")
    public EnvioResponse iniciarTransporte(@PathVariable String id) {
        EnvioId envioId = new EnvioId(id);
        envios.iniciarTransporte(envioId);
        return EnvioResponse.desde(envios.buscarPorId(envioId));
    }

    /**
     * Cierra el envío. Cruza a alertas-service: 200 si no hay alertas críticas sin resolver,
     * 409 si las hay, 503 si alertas-service no responde (nunca se cierra a ciegas).
     */
    @PostMapping("/{id}/cerrar")
    public EnvioResponse cerrar(@PathVariable String id) {
        EnvioId envioId = new EnvioId(id);
        envios.cerrar(envioId);
        return EnvioResponse.desde(envios.buscarPorId(envioId));
    }
}
