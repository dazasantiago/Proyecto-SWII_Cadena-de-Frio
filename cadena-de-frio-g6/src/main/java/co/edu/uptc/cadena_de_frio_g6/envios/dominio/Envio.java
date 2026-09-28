package co.edu.uptc.cadena_de_frio_g6.envios.dominio;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "envios")
public class Envio {

    @EmbeddedId
    @AttributeOverride(name = "valor", column = @Column(name = "id"))
    private EnvioId id;

    @Embedded
    private CondicionRequerida condicionRequerida;

    @Enumerated(EnumType.STRING)
    private EstadoEnvio estado;

    protected Envio() {
    }

    // Publico (no de paquete) desde el taller de arquitectura hexagonal: EnvioFactory quedo en
    // envios.aplicacion, un paquete distinto, y ya no puede llamar a un constructor de paquete.
    // "EnvioFactory es el unico punto de construccion" pasa a ser una convencion, no una regla
    // que el compilador imponga (igual que Investigador en rica-api).
    public Envio(EnvioId id, CondicionRequerida condicionRequerida) {
        this.id = id;
        this.condicionRequerida = condicionRequerida;
        this.estado = EstadoEnvio.REGISTRADO;
    }

    public EnvioId id() {
        return id;
    }

    public CondicionRequerida condicionRequerida() {
        return condicionRequerida;
    }

    public EstadoEnvio estado() {
        return estado;
    }

    public void iniciarTransporte() {
        if (estado != EstadoEnvio.REGISTRADO) {
            throw new TransicionEnvioInvalidaException(id, estado, EstadoEnvio.EN_TRANSITO);
        }
        estado = EstadoEnvio.EN_TRANSITO;
    }

    public void cerrar() {
        if (estado != EstadoEnvio.EN_TRANSITO) {
            throw new TransicionEnvioInvalidaException(id, estado, EstadoEnvio.CERRADO);
        }
        estado = EstadoEnvio.CERRADO;
    }
}
