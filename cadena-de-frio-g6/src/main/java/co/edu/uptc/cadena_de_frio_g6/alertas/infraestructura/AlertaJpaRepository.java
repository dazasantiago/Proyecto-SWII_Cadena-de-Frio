package co.edu.uptc.cadena_de_frio_g6.alertas.infraestructura;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Spring Data: el "rol de puerto secundario" que hoy cumple la
 * infraestructura, pero no el puerto en sí — {@link AlertaRepositoryAdapter} lo envuelve
 * para exponer solo {@link co.edu.uptc.cadena_de_frio_g6.alertas.aplicacion.AlertaRepository}.
 */
interface AlertaJpaRepository extends JpaRepository<AlertaJpaEntity, UUID> {

    boolean existsByEnvioIdAndSeveridadAndResueltaFalse(UUID envioId, String severidad);
}
