package co.edu.uptc.cadena_de_frio_g6.alertas.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.alertas.dominio.Alerta;
import java.util.UUID;

/**
 * Puerto secundario mínimo: solo los dos métodos que {@link AlertaService} llama de
 * verdad. No expone lo que un {@code JpaRepository} regalaría (findAll, deleteById, ...)
 * porque ningún caso de uso de este subdominio los necesita hoy.
 */
public interface AlertaRepository {

    void guardar(Alerta alerta);

    boolean existeAlertaCriticaSinResolver(UUID envioId);
}
