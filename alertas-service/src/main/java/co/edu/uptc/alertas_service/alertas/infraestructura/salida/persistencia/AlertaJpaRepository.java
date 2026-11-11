package co.edu.uptc.alertas_service.alertas.infraestructura.salida.persistencia;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Spring Data: el "rol de puerto secundario" que hoy cumple la
 * infraestructura, pero no el puerto en sí — {@link AlertaRepositoryAdapter} lo envuelve
 * para exponer solo {@link co.edu.uptc.alertas_service.alertas.aplicacion.AlertaRepository}.
 */
interface AlertaJpaRepository extends JpaRepository<AlertaJpaEntity, UUID> {

    boolean existsByEnvioIdAndSeveridadAndResueltaFalse(UUID envioId, String severidad);
}
