package co.edu.uptc.cadena_de_frio_g6.alertas.aplicacion;

import java.util.UUID;

/**
 * Puerto primario para otros subdominios (p. ej. {@code CierreEnvioService} de Envíos,
 * ver evidencias/dev-3/limite-agregado.md §4): consultar si un envío tiene alertas
 * críticas sin resolver, sin acceder directamente a la base de datos de Alertas.
 */
public interface ConsultaAlertasCriticasUseCase {

    boolean hayAlertaCriticaSinResolver(UUID envioId);
}
