package co.edu.uptc.sensores.aplicacion;

import co.edu.uptc.sensores.dominio.LecturaFueraDeRangoDetectada;

public interface EventoLecturaPublisher {
    void publicar(LecturaFueraDeRangoDetectada evento);
}
