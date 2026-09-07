package com.universidad.compras.estado;

import com.universidad.compras.modelo.Solicitud;
import com.universidad.compras.notificacion.PublicadorCambioEstado;

// Punto de entrada de las operaciones sobre una solicitud. No decide nada
// por si mismo: delega cada operacion en el objeto de estado actual, que es
// quien sabe si la operacion es valida y a que estado se transiciona.
public class ContextoSolicitud {

    private final Solicitud solicitud;
    private final PublicadorCambioEstado publicador;
    private EstadoSolicitud estado;

    public ContextoSolicitud(Solicitud solicitud) {
        this(solicitud, PublicadorCambioEstado.porDefecto());
    }

    public ContextoSolicitud(Solicitud solicitud, PublicadorCambioEstado publicador) {
        this.solicitud = solicitud;
        this.publicador = publicador;
        this.estado = FabricaEstados.desde(solicitud.getEstado());
    }

    public String aprobar()  { return estado.aprobar(this); }
    public String rechazar() { return estado.rechazar(this); }
    public String ejecutar() { return estado.ejecutar(this); }
    public String cancelar() { return estado.cancelar(this); }

    // Unico camino para cambiar de estado: actualiza el objeto de estado y
    // pasa el cambio por el publicador de la Necesidad 3.
    void transicionarA(EstadoSolicitud nuevoEstado, String detalle) {
        this.estado = nuevoEstado;
        publicador.cambiarEstado(solicitud, nuevoEstado.nombre(), detalle);
    }

    public String estadoActual() {
        return estado.nombre();
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }
}
