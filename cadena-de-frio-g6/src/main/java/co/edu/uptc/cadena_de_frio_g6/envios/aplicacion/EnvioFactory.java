package co.edu.uptc.cadena_de_frio_g6.envios.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.CondicionRequerida;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.RangoHumedad;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.RangoTemperatura;
import org.springframework.stereotype.Component;

/**
 * Único punto de construcción de un Envio, por convención (ver la nota en Envio sobre el
 * constructor público). Recibe los campos sueltos, no un Envio pre-armado.
 */
@Component
public class EnvioFactory {

    public Envio crear(String id, double temperaturaMinima, double temperaturaMaxima,
            double humedadMinima, double humedadMaxima) {
        EnvioId envioId = new EnvioId(id);
        CondicionRequerida condicionRequerida = new CondicionRequerida(
                new RangoTemperatura(temperaturaMinima, temperaturaMaxima),
                new RangoHumedad(humedadMinima, humedadMaxima));
        return new Envio(envioId, condicionRequerida);
    }
}
