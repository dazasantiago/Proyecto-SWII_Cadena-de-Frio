package co.edu.uptc.alertas_service.alertas.dominio;

public class LecturasConsecutivasNegativasException extends RuntimeException {

    public LecturasConsecutivasNegativasException(int lecturasFueraDeRangoConsecutivas) {
        super("El número de lecturas fuera de rango consecutivas no puede ser negativo ("
                + lecturasFueraDeRangoConsecutivas + ")");
    }
}
