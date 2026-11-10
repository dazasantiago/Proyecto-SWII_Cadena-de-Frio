package co.edu.uptc.alertas_service.alertas.dominio;

import java.util.Set;

public record SeveridadAlerta(String nivel) {

    public static final String NORMAL = "NORMAL";
    public static final String CRITICA = "CRITICA";

    private static final Set<String> NIVELES_VALIDOS = Set.of(NORMAL, CRITICA);

    public SeveridadAlerta {
        if (nivel == null || !NIVELES_VALIDOS.contains(nivel)) {
            throw new SeveridadInvalidaException(nivel);
        }
    }

    public static SeveridadAlerta normal() {
        return new SeveridadAlerta(NORMAL);
    }

    public static SeveridadAlerta critica() {
        return new SeveridadAlerta(CRITICA);
    }

    public boolean esCritica() {
        return CRITICA.equals(nivel);
    }
}
