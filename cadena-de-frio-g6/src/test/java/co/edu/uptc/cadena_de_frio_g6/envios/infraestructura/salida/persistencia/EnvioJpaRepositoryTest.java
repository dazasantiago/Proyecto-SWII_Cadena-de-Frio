package co.edu.uptc.cadena_de_frio_g6.envios.infraestructura.salida.persistencia;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.uptc.cadena_de_frio_g6.envios.aplicacion.EnvioFactory;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/** Confirma que el @EmbeddedId y los @Embedded anidados realmente se guardan y se leen de H2. */
@SpringBootTest
@Transactional
class EnvioJpaRepositoryTest {

    @Autowired
    private EnvioJpaRepository envioJpaRepository;

    private final EnvioFactory envioFactory = new EnvioFactory();

    @Test
    void guardaYRecuperaUnEnvioConSuCondicionRequeridaCompleta() {
        Envio envio = envioFactory.crear("ENV-001", 2.0, 8.0, 60.0, 80.0);

        envioJpaRepository.save(envio);
        Optional<Envio> recuperado = envioJpaRepository.findById(new EnvioId("ENV-001"));

        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().condicionRequerida()).isEqualTo(envio.condicionRequerida());
        assertThat(recuperado.get().estado()).isEqualTo(envio.estado());
    }
}
