package co.edu.uptc.cadena_de_frio_g6.alertas.infraestructura;

import co.edu.uptc.cadena_de_frio_g6.alertas.aplicacion.AlertaRepository;
import co.edu.uptc.cadena_de_frio_g6.alertas.dominio.Alerta;
import co.edu.uptc.cadena_de_frio_g6.alertas.dominio.SeveridadAlerta;
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
