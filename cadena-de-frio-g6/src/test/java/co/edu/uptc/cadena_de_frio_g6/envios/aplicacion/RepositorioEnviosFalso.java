package co.edu.uptc.cadena_de_frio_g6.envios.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.envios.dominio.Envio;
import co.edu.uptc.cadena_de_frio_g6.envios.dominio.EnvioId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class RepositorioEnviosFalso implements RepositorioEnvios {

    private final Map<EnvioId, Envio> almacen = new LinkedHashMap<>();

    @Override
    public List<Envio> listarTodos() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public Optional<Envio> buscarPorId(EnvioId id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public Envio guardar(Envio envio) {
        almacen.put(envio.id(), envio);
        return envio;
    }
}
