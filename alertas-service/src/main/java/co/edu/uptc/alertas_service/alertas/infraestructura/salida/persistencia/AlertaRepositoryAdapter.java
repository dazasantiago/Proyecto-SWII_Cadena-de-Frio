package co.edu.uptc.alertas_service.alertas.infraestructura.salida.persistencia;

import co.edu.uptc.alertas_service.alertas.aplicacion.AlertaRepository;
import co.edu.uptc.alertas_service.alertas.dominio.Alerta;
import co.edu.uptc.alertas_service.alertas.dominio.SeveridadAlerta;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador explícito del puerto secundario {@link AlertaRepository}: envuelve el
 * repositorio de Spring Data y traduce entre el modelo de dominio y el de persistencia.
 */
@Repository
class AlertaRepositoryAdapter implements AlertaRepository {

    private final AlertaJpaRepository jpaRepository;

    AlertaRepositoryAdapter(AlertaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void guardar(Alerta alerta) {
        jpaRepository.save(aEntidad(alerta));
    }

    @Override
    public boolean existeAlertaCriticaSinResolver(UUID envioId) {
        return jpaRepository.existsByEnvioIdAndSeveridadAndResueltaFalse(envioId, SeveridadAlerta.CRITICA);
    }

    @Override
    public Optional<Alerta> buscarPorId(UUID alertaId) {
        return jpaRepository.findById(alertaId).map(this::aDominio);
    }

    private Alerta aDominio(AlertaJpaEntity e) {
        return Alerta.reconstituir(e.getId(), e.getEnvioId(), e.getSensorId(), e.getFechaGeneracion(),
                new SeveridadAlerta(e.getSeveridad()), e.isResuelta());
    }

    private AlertaJpaEntity aEntidad(Alerta alerta) {
        return new AlertaJpaEntity(
                alerta.id(),
                alerta.envioId(),
                alerta.sensorId(),
                alerta.fechaGeneracion(),
                alerta.severidad().nivel(),
                alerta.resuelta());
    }
}
