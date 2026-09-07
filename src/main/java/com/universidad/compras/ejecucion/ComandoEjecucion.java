package com.universidad.compras.ejecucion;

import com.universidad.compras.modelo.Solicitud;

// Cada operacion que el equipo de Compras ejecuta sobre una solicitud
// aprobada queda encapsulada como un objeto que sabe hacerse y deshacerse
// por si mismo. El invocador no conoce PresupuestoService ni
// OrdenCompraService: solo llama ejecutar() y deshacer().
public interface ComandoEjecucion {

    void ejecutar();

    void deshacer();

    String descripcion();

    Solicitud getSolicitud();
}
