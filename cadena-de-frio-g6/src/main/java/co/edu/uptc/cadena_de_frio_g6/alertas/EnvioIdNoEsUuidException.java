package co.edu.uptc.cadena_de_frio_g6.alertas;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;

/**
 * El contrato de identidad entre Envíos y Alertas espera que {@code EnvioId.valor()} sea un UUID
 * en texto (ver {@code ConsultaAlertasCriticasImpl}); llegó un valor que no lo es.
 */
public class EnvioIdNoEsUuidException extends RuntimeException {

    public EnvioIdNoEsUuidException(EnvioId envioId) {
        super("El id de envío '" + envioId.valor() + "' no es un UUID válido; "
                + "Alertas identifica los envíos por UUID");
    }
}
