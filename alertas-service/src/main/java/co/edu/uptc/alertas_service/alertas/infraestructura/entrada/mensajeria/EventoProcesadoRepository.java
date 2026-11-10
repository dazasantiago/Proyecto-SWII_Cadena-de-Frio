package co.edu.uptc.alertas_service.alertas.infraestructura.entrada.mensajeria;

import org.springframework.data.jpa.repository.JpaRepository;

interface EventoProcesadoRepository extends JpaRepository<EventoProcesado, String> {
}
