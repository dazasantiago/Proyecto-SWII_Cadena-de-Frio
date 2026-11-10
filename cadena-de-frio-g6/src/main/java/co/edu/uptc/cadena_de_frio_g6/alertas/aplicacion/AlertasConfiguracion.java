package co.edu.uptc.cadena_de_frio_g6.alertas.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.alertas.dominio.AlertaFactory;
import co.edu.uptc.cadena_de_frio_g6.alertas.dominio.EscalamientoAlertaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cablea las clases de dominio como beans en el borde de la aplicación. El dominio
 * ({@code alertas.dominio}) no lleva anotaciones de Spring: esta es la única clase
 * que sabe que existe un framework detrás.
 */
@Configuration
class AlertasConfiguracion {

    @Bean
    AlertaFactory alertaFactory() {
        return new AlertaFactory();
    }

    @Bean
    EscalamientoAlertaService escalamientoAlertaService() {
        return new EscalamientoAlertaService();
    }
}
