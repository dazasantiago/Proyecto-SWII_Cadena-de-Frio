package co.edu.uptc.alertas_service.alertas.aplicacion;

import co.edu.uptc.alertas_service.alertas.dominio.Alerta;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto secundario mínimo: solo los dos métodos que {@link AlertaService} llama de
 * verdad. No expone lo que un {@code JpaRepository} regalaría (findAll, deleteById, ...)
 * porque ningún caso de uso de este subdominio los necesita hoy.
 */
public interface AlertaRepository {

    void guardar(Alerta alerta);

    boolean existeAlertaCriticaSinResolver(UUID envioId);

    Optional<Alerta> buscarPorId(UUID alertaId);
}
