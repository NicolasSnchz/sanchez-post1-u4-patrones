package com.universidad.compras.notificacion;

import com.universidad.compras.modelo.Solicitud;

// Informacion que viaja del publicador a cada suscriptor cuando una
// solicitud cambia de estado. Los suscriptores no consultan la solicitud
// por su cuenta: reciben aqui todo lo que necesitan para reaccionar.
public class EventoCambioEstado {
    private final Solicitud solicitud;
    private final String estadoAnterior;
    private final String estadoNuevo;
    private final String detalle;

    public EventoCambioEstado(Solicitud solicitud, String estadoAnterior, String estadoNuevo, String detalle) {
        this.solicitud = solicitud;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.detalle = detalle;
    }

    public Solicitud getSolicitud()  { return solicitud; }
    public String getEstadoAnterior(){ return estadoAnterior; }
    public String getEstadoNuevo()   { return estadoNuevo; }
    public String getDetalle()       { return detalle; }
}
