package com.universidad.compras.ejecucion;

import com.universidad.compras.modelo.Solicitud;

public class ReservarPresupuestoComando implements ComandoEjecucion {

    private final PresupuestoService presupuestoService;
    private final Solicitud solicitud;
    private boolean reservado;

    public ReservarPresupuestoComando(PresupuestoService presupuestoService, Solicitud solicitud) {
        this.presupuestoService = presupuestoService;
        this.solicitud = solicitud;
    }

    @Override
    public void ejecutar() {
        reservado = presupuestoService.reservar(solicitud.getCentroCosto(), solicitud.getMonto());
    }

    @Override
    public void deshacer() {
        if (reservado) {
            presupuestoService.liberar(solicitud.getCentroCosto(), solicitud.getMonto());
            reservado = false;
        }
    }

    @Override
    public String descripcion() {
        return "Reserva de presupuesto en " + solicitud.getCentroCosto() + " por $" + solicitud.getMonto();
    }

    @Override
    public Solicitud getSolicitud() {
        return solicitud;
    }
}
