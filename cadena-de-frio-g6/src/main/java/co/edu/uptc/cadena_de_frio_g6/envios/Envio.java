package co.edu.uptc.cadena_de_frio_g6.envios;


public class Envio {

    private final EnvioId id;
    private final RangoTemperatura condicionRequerida;
    private EstadoEnvio estado;

    Envio(EnvioId id, RangoTemperatura condicionRequerida) {
        this.id = id;
        this.condicionRequerida = condicionRequerida;
        this.estado = EstadoEnvio.REGISTRADO;
    }

    public EnvioId id() {
        return id;
    }

    public RangoTemperatura condicionRequerida() {
        return condicionRequerida;
    }

    public EstadoEnvio estado() {
        return estado;
    }

    public void iniciarTransporte() {
        if (estado != EstadoEnvio.REGISTRADO) {
            throw new TransicionEnvioInvalidaException(id, estado, EstadoEnvio.EN_TRANSITO);
        }
        estado = EstadoEnvio.EN_TRANSITO;
    }

    public void cerrar() {
        if (estado != EstadoEnvio.EN_TRANSITO) {
            throw new TransicionEnvioInvalidaException(id, estado, EstadoEnvio.CERRADO);
        }
        estado = EstadoEnvio.CERRADO;
    }
}
