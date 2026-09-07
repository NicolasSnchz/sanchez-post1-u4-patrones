package com.universidad.compras.estado;

// Cada estado por el que pasa una solicitud es un objeto que sabe que
// operaciones se permiten mientras la solicitud esta en el. La solicitud no
// pregunta por su estado con if/else: delega la operacion en el objeto que
// representa el estado en que se encuentra en ese momento.
public interface EstadoSolicitud {

    String nombre();

    String aprobar(ContextoSolicitud contexto);

    String rechazar(ContextoSolicitud contexto);

    String ejecutar(ContextoSolicitud contexto);

    String cancelar(ContextoSolicitud contexto);
}
