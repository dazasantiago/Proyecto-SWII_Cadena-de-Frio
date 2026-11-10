package co.edu.uptc.cadena_de_frio_g6.alertas.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.alertas.dominio.Alerta;

/**
 * Puerto primario: generar (y evaluar el escalamiento de) una alerta a partir de una
 * lectura fuera de rango. Lo invoca quien detecta la condición, p. ej. un adaptador REST
 * o un listener del evento {@code LecturaFueraDeRangoDetectada} de Lecturas de Sensores.
 */
public interface GenerarAlertaUseCase {

    Alerta generarAlerta(GenerarAlertaComando comando);
}
