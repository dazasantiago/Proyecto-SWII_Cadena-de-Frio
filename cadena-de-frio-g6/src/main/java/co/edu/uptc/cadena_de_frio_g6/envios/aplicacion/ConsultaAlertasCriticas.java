package co.edu.uptc.cadena_de_frio_g6.envios.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;

public interface ConsultaAlertasCriticas {

    boolean tieneAlertasCriticasSinResolver(EnvioId envioId);
}
