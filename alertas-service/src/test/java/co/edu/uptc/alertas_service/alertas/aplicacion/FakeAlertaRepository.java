package co.edu.uptc.alertas_service.alertas.aplicacion;

import co.edu.uptc.alertas_service.alertas.dominio.Alerta;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Fake hecho a mano del puerto secundario: un mapa en memoria, sin Spring ni Mockito.
 * Permite ejercitar {@link AlertaService} de punta a punta sin levantar contexto ni BD.
 */
class FakeAlertaRepository implements AlertaRepository {

    private final Map<UUID, Alerta> alertas = new HashMap<>();

    @Override
    public void guardar(Alerta alerta) {
        alertas.put(alerta.id(), alerta);
    }

    @Override
    public boolean existeAlertaCriticaSinResolver(UUID envioId) {
        return alertas.values().stream()
                .anyMatch(alerta -> alerta.envioId().equals(envioId) && alerta.esCritica() && !alerta.resuelta());
    }

    @Override
    public Optional<Alerta> buscarPorId(UUID alertaId) {
        return Optional.ofNullable(alertas.get(alertaId));
    }
}
