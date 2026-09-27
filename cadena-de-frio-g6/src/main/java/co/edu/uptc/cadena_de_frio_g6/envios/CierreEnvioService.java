package co.edu.uptc.cadena_de_frio_g6.envios;

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
