package co.edu.uptc.cadena_de_frio_g6.envios;

public record EnvioId(String valor) {

    public EnvioId {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El identificador del envío no puede estar vacío");
        }
    }
}
