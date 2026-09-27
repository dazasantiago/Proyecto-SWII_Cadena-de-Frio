package co.edu.uptc.cadena_de_frio_g6.alertas;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

/**
 * Implementación en memoria de {@link AlertaRepository}: un placeholder mientras no hay
 * persistencia real (JPA llega con la arquitectura hexagonal). No usar en producción.
 */
@Repository
public class InMemoryAlertaRepository implements AlertaRepository {

    private final Map<UUID, Alerta> alertasPorId = new ConcurrentHashMap<>();

    @Override
    public void guardar(Alerta alerta) {
        alertasPorId.put(alerta.id(), alerta);
    }

    @Override
    public List<Alerta> buscarPorEnvio(UUID envioId) {
        return alertasPorId.values().stream()
                .filter(alerta -> alerta.envioId().equals(envioId))
                .collect(Collectors.toList());
    }
}
