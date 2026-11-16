package co.edu.uptc.cadena_de_frio_g6.envios.infraestructura.salida.persistencia;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.uptc.cadena_de_frio_g6.envios.aplicacion.EnvioFactory;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Confirma que el @EmbeddedId y los @Embedded anidados realmente se guardan y se leen de
 * PostgreSQL (cadena-db, con las mismas migraciones Flyway que usa el servicio).
 *
 * <p>Necesita cadena-db levantada ({@code docker compose up -d} en esta carpeta, o el servicio
 * postgres del CI). Si no responde, la prueba se omite con el motivo en el reporte en vez de
 * fallar: el resto de {@code mvnw test} sigue siendo útil sin Docker, y el CI, que sí levanta la
 * base, la ejecuta siempre.</p>
 */
@SpringBootTest
@Transactional
@EnabledIf(value = "baseDeDatosDisponible",
        disabledReason = "cadena-db no responde en localhost:5435: levántela con docker compose up -d")
class EnvioJpaRepositoryTest {

    /** Mismos datos de conexión que application.yml y docker-compose.yml. */
    static boolean baseDeDatosDisponible() {
        try (Connection conexion = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5435/cadena_de_frio?connectTimeout=2", "cadena", "cadena")) {
            return conexion.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

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
