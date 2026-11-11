package co.edu.uptc.cadena_de_frio_g6.envios.infraestructura.entrada.web;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;

/** Lo que se devuelve de un envío: nunca se expone la entidad JPA directamente. */
public record EnvioResponse(String id, double temperaturaMinima, double temperaturaMaxima,
                            double humedadMinima, double humedadMaxima, String estado) {

    static EnvioResponse desde(Envio envio) {
        var condicion = envio.condicionRequerida();
        return new EnvioResponse(envio.id().valor(),
                condicion.rangoTemperatura().minimo(), condicion.rangoTemperatura().maximo(),
                condicion.rangoHumedad().minimo(), condicion.rangoHumedad().maximo(),
                envio.estado().name());
    }
}
