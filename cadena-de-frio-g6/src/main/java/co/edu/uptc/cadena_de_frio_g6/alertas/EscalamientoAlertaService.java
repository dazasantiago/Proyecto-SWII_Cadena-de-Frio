package co.edu.uptc.cadena_de_frio_g6.alertas;

/**
 * Decide si una alerta debe escalar a severidad crítica.
 * Umbral por defecto: 3 lecturas fuera de rango consecutivas (ver evidencias/dev-3/glosario.md, §5 — a confirmar).
 */
public class EscalamientoAlertaService {

    public static final int LECTURAS_CONSECUTIVAS_PARA_ESCALAR = 3;

    public void evaluar(Alerta alerta, int lecturasFueraDeRangoConsecutivas) {
        if (lecturasFueraDeRangoConsecutivas < 0) {
            throw new LecturasConsecutivasNegativasException(lecturasFueraDeRangoConsecutivas);
        }
        if (lecturasFueraDeRangoConsecutivas >= LECTURAS_CONSECUTIVAS_PARA_ESCALAR) {
            alerta.marcarComoCritica();
        }
    }
}
