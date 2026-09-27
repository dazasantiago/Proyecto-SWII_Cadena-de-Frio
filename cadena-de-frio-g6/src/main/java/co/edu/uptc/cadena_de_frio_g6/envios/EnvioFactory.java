package co.edu.uptc.cadena_de_frio_g6.envios;

/**
 * Único punto de construcción de un Envio: nadie fuera de este paquete arma un Envio directamente
 * (el constructor de Envio es de paquete). Recibe los campos sueltos, no un Envio pre-armado.
 */
public class EnvioFactory {

    public Envio crear(String id, double temperaturaMinima, double temperaturaMaxima) {
        EnvioId envioId = new EnvioId(id);
        RangoTemperatura condicionRequerida = new RangoTemperatura(temperaturaMinima, temperaturaMaxima);
        return new Envio(envioId, condicionRequerida);
    }
}
