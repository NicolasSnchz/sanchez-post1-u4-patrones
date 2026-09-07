package com.universidad.compras.estado;

// Comportamiento por defecto: toda operacion se rechaza sin cambiar el
// estado. Cada estado concreto solo declara lo que si permite, y un estado
// nuevo se agrega escribiendo una clase mas, sin revisar los demas.
public abstract class EstadoBase implements EstadoSolicitud {

    @Override
    public String aprobar(ContextoSolicitud contexto) {
        return noPermitida("aprobar");
    }

    @Override
    public String rechazar(ContextoSolicitud contexto) {
        return noPermitida("rechazar");
    }

    @Override
    public String ejecutar(ContextoSolicitud contexto) {
        return noPermitida("ejecutar");
    }

    @Override
    public String cancelar(ContextoSolicitud contexto) {
        return noPermitida("cancelar");
    }

    protected String noPermitida(String operacion) {
        return "Error: no se puede " + operacion + " una solicitud en estado " + nombre();
    }
}
