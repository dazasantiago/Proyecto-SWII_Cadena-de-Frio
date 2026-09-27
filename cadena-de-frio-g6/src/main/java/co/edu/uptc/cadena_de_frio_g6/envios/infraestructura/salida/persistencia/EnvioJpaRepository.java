package co.edu.uptc.cadena_de_frio_g6.envios.infraestructura.salida.persistencia;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvioJpaRepository extends JpaRepository<Envio, EnvioId> {
}
