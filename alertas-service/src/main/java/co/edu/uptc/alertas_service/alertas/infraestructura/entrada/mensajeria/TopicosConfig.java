package co.edu.uptc.alertas_service.alertas.infraestructura.entrada.mensajeria;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** Tópicos que consume Alertas. Declararlos permite arrancar aunque sensores no haya publicado aún. */
@Configuration
public class TopicosConfig {

    /** Contrato con Lecturas de Sensores: evento LecturaFueraDeRangoDetectada, clave = envioId. */
    public static final String LECTURAS_FUERA_DE_RANGO = "sensores.lecturas-fuera-de-rango";

    @Bean
    public NewTopic lecturasFueraDeRango() {
        return TopicBuilder.name(LECTURAS_FUERA_DE_RANGO).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic lecturasFueraDeRangoDlt() {
        return TopicBuilder.name(LECTURAS_FUERA_DE_RANGO + ".DLT").partitions(1).replicas(1).build();
    }
}
