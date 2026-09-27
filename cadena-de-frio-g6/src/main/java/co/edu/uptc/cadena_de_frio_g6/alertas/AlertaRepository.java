package co.edu.uptc.cadena_de_frio_g6.alertas;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de persistencia de Alertas. La implementación en memoria ({@link InMemoryAlertaRepository})
 * es temporal, hasta que el curso introduzca la capa de persistencia real (arquitectura hexagonal).
 */
public interface AlertaRepository {

    void guardar(Alerta alerta);

    List<Alerta> buscarPorEnvio(UUID envioId);
}
