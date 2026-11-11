package co.edu.uptc.eureka_server;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.core.io.ClassPathResource;

/**
 * Protege la configuración del registro sin levantar el servidor: si alguien la cambia por error,
 * los demás servicios dejan de encontrarlo o Eureka empieza a replicarse contra sí mismo.
 */
class ConfiguracionEurekaTest {

    private static Properties configuracion;

    @BeforeAll
    static void leerApplicationYml() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        configuracion = yaml.getObject();
    }

    @Test
    void escuchaEnElPuertoQueUsanLosClientes() {
        // alertas-service y cadena-de-frio-g6 apuntan a http://localhost:8761/eureka
        assertThat(configuracion.getProperty("server.port")).isEqualTo("8761");
    }

    @Test
    void noSeRegistraNiConsultaComoSiFueraUnCliente() {
        assertThat(configuracion.getProperty("eureka.client.register-with-eureka")).isEqualTo("false");
        assertThat(configuracion.getProperty("eureka.client.fetch-registry")).isEqualTo("false");
    }

    @Test
    void declaraSuHostnameParaNoReplicarseContraSiMismo() {
        // Sin esto, dentro de un contenedor Eureka toma localhost:8761 como si fuera otro nodo.
        assertThat(configuracion.getProperty("eureka.instance.hostname")).isEqualTo("localhost");
    }

    @Test
    void laAplicacionActivaElServidorEureka() {
        assertThat(EurekaServerApplication.class.isAnnotationPresent(EnableEurekaServer.class)).isTrue();
    }
}
