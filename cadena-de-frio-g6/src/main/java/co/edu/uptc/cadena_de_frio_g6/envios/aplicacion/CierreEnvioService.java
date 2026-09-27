package co.edu.uptc.cadena_de_frio_g6.envios.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioConAlertasCriticasException;
import org.springframework.stereotype.Service;

/**
 * Servicio de Dominio: cerrar un envío depende de información que vive en otro contexto (las alertas),
 * por lo que no pertenece a Envio por sí solo. No guarda estado propio.
 */
@Service
public class CierreEnvioService {

    private final ConsultaAlertasCriticas consultaAlertasCriticas;

    public CierreEnvioService(ConsultaAlertasCriticas consultaAlertasCriticas) {
        this.consultaAlertasCriticas = consultaAlertasCriticas;
    }

    public void cerrar(Envio envio) {
        if (consultaAlertasCriticas.tieneAlertasCriticasSinResolver(envio.id())) {
            throw new EnvioConAlertasCriticasException(envio.id());
        }
        envio.cerrar();
    }
}
