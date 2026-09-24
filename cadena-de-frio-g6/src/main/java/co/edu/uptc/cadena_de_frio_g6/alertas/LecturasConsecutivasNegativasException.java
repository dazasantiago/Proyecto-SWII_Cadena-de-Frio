package co.edu.uptc.cadena_de_frio_g6.alertas;

public class LecturasConsecutivasNegativasException extends RuntimeException {

    public LecturasConsecutivasNegativasException(int lecturasFueraDeRangoConsecutivas) {
        super("El número de lecturas fuera de rango consecutivas no puede ser negativo ("
                + lecturasFueraDeRangoConsecutivas + ")");
    }
}
