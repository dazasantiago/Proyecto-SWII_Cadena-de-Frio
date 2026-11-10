package co.edu.uptc.cadena_de_frio_g6.alertas.aplicacion;

import co.edu.uptc.cadena_de_frio_g6.alertas.dominio.Alerta;
import co.edu.uptc.cadena_de_frio_g6.alertas.dominio.AlertaFactory;
import co.edu.uptc.cadena_de_frio_g6.alertas.dominio.EscalamientoAlertaService;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Implementa ambos puertos primarios del subdominio orquestando el dominio
 * ({@link AlertaFactory}, {@link EscalamientoAlertaService}) y el puerto secundario
 * ({@link AlertaRepository}), sin depender de ningún detalle de infraestructura.
 */
@Service
public class AlertaService implements GenerarAlertaUseCase, ConsultaAlertasCriticasUseCase {

    private final AlertaFactory alertaFactory;
    private final EscalamientoAlertaService escalamientoAlertaService;
    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaFactory alertaFactory, EscalamientoAlertaService escalamientoAlertaService,
            AlertaRepository alertaRepository) {
        this.alertaFactory = alertaFactory;
        this.escalamientoAlertaService = escalamientoAlertaService;
        this.alertaRepository = alertaRepository;
    }

    @Override
    public Alerta generarAlerta(GenerarAlertaComando comando) {
        Alerta alerta = alertaFactory.generar(comando.envioId(), comando.sensorId());
        escalamientoAlertaService.evaluar(alerta, comando.lecturasFueraDeRangoConsecutivas());
        alertaRepository.guardar(alerta);
        return alerta;
    }

    @Override
    public boolean hayAlertaCriticaSinResolver(UUID envioId) {
        return alertaRepository.existeAlertaCriticaSinResolver(envioId);
    }
}
